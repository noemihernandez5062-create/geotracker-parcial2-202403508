import UsuarioModel from '../models/UsuarioModel';

export class UsuarioRepository {
  async create(data: {
    nombre: string;
    correo: string;
    telefono?: string | null;
    passwordHash: string;
  }) {
    return UsuarioModel.create({
      nombre: data.nombre,
      correo: data.correo,
      telefono: data.telefono ?? null,
      passwordHash: data.passwordHash
    });
  }

  async findByCorreo(correo: string) {
    return UsuarioModel.findOne({ where: { correo } });
  }

  async findById(id: number) {
    return UsuarioModel.findByPk(id);
  }

  async updateProfile(
    id: number,
    data: { nombre?: string; correo?: string; telefono?: string | null }
  ) {
    const usuario = await this.findById(id);
    if (!usuario) return null;

    if (data.nombre !== undefined) usuario.nombre = data.nombre;
    if (data.correo !== undefined) usuario.correo = data.correo;
    if (data.telefono !== undefined) usuario.telefono = data.telefono;

    await usuario.save();
    return usuario;
  }

  async findFoto(id: number) {
    return UsuarioModel.scope('conFoto').findByPk(id, {
      attributes: ['id', 'foto', 'fotoTipo']
    });
  }

  async updateFoto(id: number, foto: Buffer, fotoTipo: string) {
    const [actualizados] = await UsuarioModel.update(
      { foto, fotoTipo },
      { where: { id } }
    );
    return actualizados > 0;
  }
}
