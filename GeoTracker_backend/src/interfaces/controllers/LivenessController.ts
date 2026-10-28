import { Response } from 'express';
import { AuthRequest } from '../middlewares/authMiddleware';
import { UsuarioRepository } from '../../infrastructure/repositories/UsuarioRepository';
import VerificacionModel from '../../infrastructure/models/VerificacionModel';
import {
  LambdaError,
  compararRostros,
  crearSesion,
  obtenerResultados
} from '../../infrastructure/aws/LivenessLambdas';

const usuarioRepository = new UsuarioRepository();

// Confianza minima (0-100) de Face Liveness para considerar que la persona es real
const UMBRAL_LIVENESS = 90;

// La Lambda de resultados devuelve un texto en lugar de la imagen cuando no hay
const SIN_IMAGEN = 'No se encontr';

function responderError(res: Response, error: unknown, mensajeGenerico: string) {
  console.error(`ERROR REAL (${mensajeGenerico}):`, error);

  if (error instanceof LambdaError && error.statusCode === 404) {
    res.status(404).json({ success: false, message: 'La sesion no existe o ya expiro', data: null });
    return;
  }

  res.status(500).json({ success: false, message: mensajeGenerico, data: null });
}

export class LivenessController {
  // POST /api/liveness/session -> { sessionId } para iniciar la prueba de vida en la app
  static async crearSesion(_req: AuthRequest, res: Response) {
    try {
      const sessionId = await crearSesion();

      res.json({
        success: true,
        message: 'Sesion de verificacion creada',
        data: { sessionId }
      });
    } catch (error) {
      responderError(res, error, 'Error al crear la sesion de verificacion');
    }
  }

  // POST /api/liveness/results { sessionId }
  // 1. Consulta el resultado de la prueba de vida.
  // 2. Si la persona es real, compara su rostro con la foto de perfil.
  // 3. Guarda la verificacion y la devuelve.
  static async obtenerResultados(req: AuthRequest, res: Response) {
    const { sessionId } = req.body ?? {};

    if (!sessionId || typeof sessionId !== 'string') {
      res.status(400).json({ success: false, message: 'Falta el sessionId', data: null });
      return;
    }

    try {
      const resultado = await obtenerResultados(sessionId);

      const confianza = typeof resultado.confidence === 'number' ? resultado.confidence : null;
      const esReal = resultado.status === 'SUCCEEDED' && (confianza ?? 0) >= UMBRAL_LIVENESS;
      const imagenReferencia = resultado.referenceImageBase64.startsWith(SIN_IMAGEN)
        ? null
        : resultado.referenceImageBase64;

      let coincide: boolean | null = null;
      let similitud: number | null = null;
      let mensaje: string;

      if (!esReal) {
        mensaje = resultado.status === 'SUCCEEDED'
          ? 'No se pudo confirmar que la persona sea real'
          : `La prueba de vida no ha terminado (estado: ${resultado.status})`;
      } else {
        const usuario = await usuarioRepository.findFoto(req.userId!);

        if (!usuario?.foto || !imagenReferencia) {
          mensaje = 'Persona real, pero el usuario no tiene foto de perfil para comparar';
        } else {
          try {
            const comparacion = await compararRostros(imagenReferencia, usuario.foto.toString('base64'));
            coincide = comparacion.match;
            similitud = comparacion.similarity;
            mensaje = coincide
              ? 'Identidad verificada'
              : 'El rostro no coincide con la foto de perfil';
          } catch (error) {
            // 400 = la foto de perfil no tiene un rostro reconocible
            if (error instanceof LambdaError && error.statusCode === 400) {
              coincide = false;
              similitud = 0;
              mensaje = 'La foto de perfil no tiene un rostro reconocible';
            } else {
              throw error;
            }
          }
        }
      }

      // Solo se guardan las sesiones que ya terminaron
      if (resultado.status !== 'CREATED' && resultado.status !== 'IN_PROGRESS') {
        await VerificacionModel.create({
          usuarioId: req.userId!,
          sessionId,
          status: resultado.status,
          confianza,
          esReal,
          coincide,
          similitud
        });
      }

      res.json({
        success: true,
        message: mensaje,
        data: {
          sessionId,
          status: resultado.status,
          confianza,
          esReal,
          coincide,
          similitud,
          verificado: esReal && coincide === true
        }
      });
    } catch (error) {
      responderError(res, error, 'Error al obtener los resultados de la verificacion');
    }
  }
}
