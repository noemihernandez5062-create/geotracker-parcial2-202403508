import { Request, Response } from 'express';

import { RegisterUser } from '../../application_use_cases/RegisterUser';
import { LoginUser } from '../../application_use_cases/LoginUser';
import { UsuarioRepository } from '../../infrastructure/repositories/UsuarioRepository';
import { usuarioSeguro } from './usuarioSeguro';

const usuarioRepository = new UsuarioRepository();

const registerUser = new RegisterUser(usuarioRepository);
const loginUser = new LoginUser(usuarioRepository);

export class AuthController {

  static async register(req: Request, res: Response) {
    try {
      const {
        nombre,
        correo,
        telefono,
        password
      } = req.body;

      if (!nombre || !correo || !password) {
        return res.status(400).json({
          success: false,
          message: 'Nombre, correo y password son obligatorios',
          data: null
        });
      }

      if (String(password).length < 5) {
        return res.status(400).json({
          success: false,
          message: 'La contrasena debe tener al menos 5 caracteres',
          data: null
        });
      }

      const usuario = await registerUser.execute({
        nombre: String(nombre).trim(),

        correo: String(correo)
          .trim()
          .toLowerCase(),

        telefono: telefono
          ? String(telefono).trim()
          : null,

        password: String(password)
      });

      return res.status(201).json({
        success: true,
        message: 'Usuario registrado correctamente',
        data: usuarioSeguro(usuario)
      });

    } catch (error: any) {

      console.error(
        'ERROR REAL AL REGISTRAR:',
        error
      );

      // RegisterUser lanza 'El correo ya esta registrado' si lo encuentra antes;
      // la restriccion UNIQUE cubre dos registros simultaneos con el mismo correo
      if (
        error?.name === 'SequelizeUniqueConstraintError' ||
        error?.message === 'El correo ya esta registrado'
      ) {
        return res.status(409).json({
          success: false,
          message: 'El correo ya se encuentra registrado',
          data: null
        });
      }

      return res.status(500).json({
        success: false,
        message: 'Error al registrar usuario',
        data: null
      });
    }
  }

  static async login(req: Request, res: Response) {
    try {
      const {
        correo,
        password
      } = req.body;

      if (!correo || !password) {
        return res.status(400).json({
          success: false,
          message: 'Correo y password son obligatorios',
          data: null
        });
      }

      const resultado = await loginUser.execute(
        String(correo)
          .trim()
          .toLowerCase(),

        String(password)
      );

      return res.status(200).json({
        success: true,
        message: 'Inicio de sesion exitoso',

        data: {
          token: resultado.token,
          usuario: usuarioSeguro(
            resultado.usuario
          )
        }
      });

    } catch (error: any) {

      console.error(
        'ERROR REAL AL INICIAR SESION:',
        error
      );

      const credencialesIncorrectas =
        error?.message ===
        'Credenciales incorrectas';

      return res
        .status(
          credencialesIncorrectas
            ? 401
            : 500
        )
        .json({
          success: false,
          message: credencialesIncorrectas
            ? 'Credenciales incorrectas'
            : 'Error al iniciar sesion',
          data: null
        });
    }
  }
}