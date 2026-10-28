import { Response } from 'express';
import { UbicacionRepository } from '../../infrastructure/repositories/UbicacionRepository';
import { AuthRequest } from '../middlewares/authMiddleware';

const ubicacionRepository = new UbicacionRepository();

function ubicacionRespuesta(ubicacion: any) {
  return {
    id: ubicacion.id,
    nombre: ubicacion.nombre,
    descripcion: ubicacion.descripcion,
    latitud: ubicacion.latitud,
    longitud: ubicacion.longitud,
    fechaCreacion: ubicacion.fechaCreacion
  };
}

function enRango(valor: unknown, minimo: number, maximo: number) {
  return typeof valor === 'number' && Number.isFinite(valor) && valor >= minimo && valor <= maximo;
}

export class UbicacionController {
  // POST /api/ubicaciones
  static async create(req: AuthRequest, res: Response) {
    try {
      const { nombre, descripcion, latitud, longitud } = req.body;

      if (!nombre || !descripcion || !String(nombre).trim() || !String(descripcion).trim()) {
        res.status(400).json({
          success: false,
          message: 'Nombre y descripcion son obligatorios',
          data: null
        });
        return;
      }

      if (!enRango(latitud, -90, 90) || !enRango(longitud, -180, 180)) {
        res.status(400).json({
          success: false,
          message: 'Latitud y longitud son obligatorias y deben ser validas',
          data: null
        });
        return;
      }

      const ubicacion = await ubicacionRepository.create({
        nombre: String(nombre).trim(),
        descripcion: String(descripcion).trim(),
        latitud,
        longitud,
        usuarioId: req.userId!
      });

      res.status(201).json({
        success: true,
        message: 'Ubicacion guardada correctamente',
        data: ubicacionRespuesta(ubicacion)
      });
    } catch (error) {
      console.error('ERROR REAL AL GUARDAR UBICACION:', error);

      res.status(500).json({
        success: false,
        message: 'Error al guardar ubicacion',
        data: null
      });
    }
  }

  // GET /api/ubicaciones: solo las del usuario del token
  static async findMine(req: AuthRequest, res: Response) {
    try {
      const ubicaciones = await ubicacionRepository.findByUsuario(req.userId!);

      res.json({
        success: true,
        message: 'Ubicaciones obtenidas correctamente',
        data: ubicaciones.map(ubicacionRespuesta)
      });
    } catch (error) {
      console.error('ERROR REAL AL OBTENER UBICACIONES:', error);

      res.status(500).json({
        success: false,
        message: 'Error al obtener ubicaciones',
        data: null
      });
    }
  }
}
