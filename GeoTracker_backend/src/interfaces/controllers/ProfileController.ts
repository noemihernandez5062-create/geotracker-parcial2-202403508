import { Response } from 'express';
import { UsuarioRepository } from '../../infrastructure/repositories/UsuarioRepository';
import { AuthRequest } from '../middlewares/authMiddleware';
import { usuarioSeguro } from './usuarioSeguro';

const usuarioRepository = new UsuarioRepository();

export class ProfileController {
  static async getProfile(req: AuthRequest, res: Response) {
    try {
      const usuario = await usuarioRepository.findById(req.userId!);

      if (!usuario) {
        res.status(404).json({
          success: false,
          message: 'Usuario no encontrado',
          data: null
        });
        return;
      }

      res.json({
        success: true,
        message: 'Perfil obtenido correctamente',
        data: usuarioSeguro(usuario)
      });
    } catch {
      res.status(500).json({
        success: false,
        message: 'Error al obtener perfil',
        data: null
      });
    }
  }

  static async updateProfile(req: AuthRequest, res: Response) {
    try {
      const { nombre, correo, telefono } = req.body;

      if (correo) {
        const correoNormalizado = String(correo).trim().toLowerCase();
        const existente = await usuarioRepository.findByCorreo(correoNormalizado);
        if (existente && existente.id !== req.userId) {
          res.status(409).json({
            success: false,
            message: 'El correo ya esta registrado',
            data: null
          });
          return;
        }
      }

      const usuario = await usuarioRepository.updateProfile(req.userId!, {
        nombre: nombre !== undefined ? String(nombre).trim() : undefined,
        correo: correo !== undefined ? String(correo).trim().toLowerCase() : undefined,
        telefono: telefono !== undefined ? (telefono ? String(telefono).trim() : null) : undefined
      });

      if (!usuario) {
        res.status(404).json({
          success: false,
          message: 'Usuario no encontrado',
          data: null
        });
        return;
      }

      res.json({
        success: true,
        message: 'Perfil actualizado correctamente',
        data: usuarioSeguro(usuario)
      });
    } catch {
      res.status(500).json({
        success: false,
        message: 'Error al actualizar perfil',
        data: null
      });
    }
  }

  // El cuerpo es la imagen en bytes (byteArray), no JSON.
  // Content-Type: image/jpeg, image/png o image/webp
  static async updateFoto(req: AuthRequest, res: Response) {
    try {
      const foto = req.body;

      if (!Buffer.isBuffer(foto) || foto.length === 0) {
        res.status(400).json({
          success: false,
          message: 'Debe enviar la imagen como image/jpeg, image/png o image/webp',
          data: null
        });
        return;
      }

      const fotoTipo = String(req.headers['content-type']).split(';')[0];
      const actualizado = await usuarioRepository.updateFoto(req.userId!, foto, fotoTipo);

      if (!actualizado) {
        res.status(404).json({
          success: false,
          message: 'Usuario no encontrado',
          data: null
        });
        return;
      }

      const usuario = await usuarioRepository.findById(req.userId!);

      res.json({
        success: true,
        message: 'Foto actualizada correctamente',
        data: usuarioSeguro(usuario)
      });
    } catch {
      res.status(500).json({
        success: false,
        message: 'Error al guardar la foto',
        data: null
      });
    }
  }

  // Devuelve la imagen en bytes, no JSON. 404 si el usuario no tiene foto.
  static async getFoto(req: AuthRequest, res: Response) {
    try {
      const usuario = await usuarioRepository.findFoto(req.userId!);

      if (!usuario || !usuario.foto) {
        res.status(404).json({
          success: false,
          message: 'El usuario no tiene foto',
          data: null
        });
        return;
      }

      res.set('Content-Type', usuario.fotoTipo || 'image/jpeg');
      res.set('Cache-Control', 'no-store');
      res.send(usuario.foto);
    } catch {
      res.status(500).json({
        success: false,
        message: 'Error al obtener la foto',
        data: null
      });
    }
  }
}
