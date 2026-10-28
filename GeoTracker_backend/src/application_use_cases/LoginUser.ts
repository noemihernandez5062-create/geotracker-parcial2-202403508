import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { UsuarioRepository } from '../infrastructure/repositories/UsuarioRepository';

export class LoginUser {
  constructor(private readonly usuarioRepository: UsuarioRepository) {}

  async execute(correo: string, password: string) {
    const usuario = await this.usuarioRepository.findByCorreo(correo);

    if (!usuario || !usuario.activo) {
      throw new Error('Credenciales incorrectas');
    }

    const passwordValido = await bcrypt.compare(password, usuario.passwordHash);
    if (!passwordValido) {
      throw new Error('Credenciales incorrectas');
    }

    const secret = process.env.JWT_SECRET;
    if (!secret) {
      throw new Error('JWT_SECRET no esta configurado');
    }

    const token = jwt.sign(
      { id: usuario.id, correo: usuario.correo, rol: usuario.rol },
      secret,
      { expiresIn: '8h' }
    );

    return { usuario, token };
  }
}
