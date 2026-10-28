import { NextFunction, Request, Response } from 'express';
import jwt from 'jsonwebtoken';
import { Rol } from '../../infrastructure/models/UsuarioModel';

export interface AuthRequest extends Request {
  userId?: number;
  userRol?: Rol;
}

export function authMiddleware(req: AuthRequest, res: Response, next: NextFunction) {
  const header = req.headers.authorization;

  if (!header || !header.startsWith('Bearer ')) {
    res.status(401).json({
      success: false,
      message: 'Token no proporcionado',
      data: null
    });
    return;
  }

  const token = header.substring(7);
  const secret = process.env.JWT_SECRET;

  if (!secret) {
    res.status(500).json({
      success: false,
      message: 'JWT_SECRET no esta configurado',
      data: null
    });
    return;
  }

  try {
    const payload = jwt.verify(token, secret) as { id: number; rol?: Rol };
    req.userId = Number(payload.id);
    // Tokens emitidos antes de existir los roles no traen rol
    req.userRol = payload.rol ?? 'CENSISTA';
    next();
  } catch {
    res.status(401).json({
      success: false,
      message: 'Token invalido o vencido',
      data: null
    });
  }
}

// Usar despues de authMiddleware: permite el paso solo a los roles indicados
export function requireRol(...roles: Rol[]) {
  return (req: AuthRequest, res: Response, next: NextFunction) => {
    if (!req.userRol || !roles.includes(req.userRol)) {
      res.status(403).json({
        success: false,
        message: 'No tiene permiso para realizar esta accion',
        data: null
      });
      return;
    }
    next();
  };
}
