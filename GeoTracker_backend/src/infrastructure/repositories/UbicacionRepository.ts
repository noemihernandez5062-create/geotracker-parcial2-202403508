import UbicacionModel from '../models/UbicacionModel';

export class UbicacionRepository {
  async create(data: {
    nombre: string;
    descripcion: string;
    latitud: number;
    longitud: number;
    usuarioId: number;
  }) {
    return UbicacionModel.create(data);
  }

  // Solo las ubicaciones del usuario indicado, la mas reciente primero
  async findByUsuario(usuarioId: number) {
    return UbicacionModel.findAll({
      where: { usuarioId },
      order: [['fechaCreacion', 'DESC']]
    });
  }
}
