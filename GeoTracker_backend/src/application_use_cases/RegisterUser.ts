import bcrypt from 'bcryptjs';
import { UsuarioRepository } from '../infrastructure/repositories/UsuarioRepository';

export class RegisterUser {
  constructor(private readonly usuarioRepository: UsuarioRepository) {}

  async execute(data: {
    nombre: string;
    correo: string;
    telefono?: string | null;
    password: string;
  }) {
    const existe = await this.usuarioRepository.findByCorreo(data.correo);
    if (existe) {
      throw new Error('El correo ya esta registrado');
    }

    const passwordHash = await bcrypt.hash(data.password, 10);

    return this.usuarioRepository.create({
      nombre: data.nombre,
      correo: data.correo,
      telefono: data.telefono ?? null,
      passwordHash
    });
  }
}
