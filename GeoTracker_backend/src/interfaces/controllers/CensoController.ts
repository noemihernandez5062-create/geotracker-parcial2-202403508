import { Response } from 'express';
import { CensoRepository } from '../../infrastructure/repositories/CensoRepository';
import { ORIGENES, Origen } from '../../infrastructure/models/CensoModel';
import { AuthRequest } from '../middlewares/authMiddleware';

const censoRepository = new CensoRepository();

function censoRespuesta(censo: any) {
  return {
    id: censo.id,
    nombre: censo.nombre,
    apellido: censo.apellido,
    totalMiembros: censo.totalMiembros,
    cantidadTelevisiones: censo.cantidadTelevisiones,
    latitud: censo.latitud,
    longitud: censo.longitud,
    origen: censo.origen,
    censista: censo.censista ? censo.censista.nombre : null,
    fechaRegistro: censo.fechaRegistro
  };
}

function esEntero(valor: unknown, minimo: number) {
  return Number.isInteger(valor) && (valor as number) >= minimo;
}

function enRango(valor: unknown, minimo: number, maximo: number) {
  return typeof valor === 'number' && Number.isFinite(valor) && valor >= minimo && valor <= maximo;
}

export class CensoController {
  static async create(req: AuthRequest, res: Response) {
    try {
      const {
        nombre,
        apellido,
        totalMiembros,
        cantidadTelevisiones,
        latitud,
        longitud
      } = req.body;

      if (!nombre || !apellido || !String(nombre).trim() || !String(apellido).trim()) {
        res.status(400).json({
          success: false,
          message: 'Nombre y apellido son obligatorios',
          data: null
        });
        return;
      }

      if (!esEntero(totalMiembros, 1) || !esEntero(cantidadTelevisiones, 0)) {
        res.status(400).json({
          success: false,
          message: 'Total de miembros debe ser 1 o mas y cantidad de televisiones 0 o mas',
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

      const censo = await censoRepository.create({
        nombre: String(nombre).trim(),
        apellido: String(apellido).trim(),
        totalMiembros,
        cantidadTelevisiones,
        latitud,
        longitud,
        usuarioId: req.userId!
      });

      res.status(201).json({
        success: true,
        message: 'Censo registrado correctamente',
        data: censoRespuesta(censo)
      });
    } catch (error) {
      console.error('ERROR REAL AL REGISTRAR CENSO:', error);

      res.status(500).json({
        success: false,
        message: 'Error al registrar censo',
        data: null
      });
    }
  }

  // GET /api/censos?origen=NORMAL|MASIVO (sin origen devuelve todos)
  static async findAll(req: AuthRequest, res: Response) {
    try {
      const origen = req.query.origen
        ? String(req.query.origen).toUpperCase()
        : undefined;

      if (origen && !ORIGENES.includes(origen as Origen)) {
        res.status(400).json({
          success: false,
          message: 'El origen debe ser NORMAL o MASIVO',
          data: null
        });
        return;
      }

      const censos = await censoRepository.findAll(origen as Origen | undefined);

      res.json({
        success: true,
        message: 'Censos obtenidos correctamente',
        data: censos.map(censoRespuesta)
      });
    } catch (error) {
      console.error('ERROR REAL AL OBTENER CENSOS:', error);

      res.status(500).json({
        success: false,
        message: 'Error al obtener censos',
        data: null
      });
    }
  }
}
