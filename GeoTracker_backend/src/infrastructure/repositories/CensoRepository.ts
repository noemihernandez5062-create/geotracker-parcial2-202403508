import CensoModel, { Origen } from '../models/CensoModel';

export class CensoRepository {
  async create(data: {
    nombre: string;
    apellido: string;
    totalMiembros: number;
    cantidadTelevisiones: number;
    latitud: number;
    longitud: number;
    usuarioId: number;
  }) {
    return CensoModel.create({
      ...data,
      origen: 'NORMAL'
    });
  }

  async findAll(origen?: Origen) {
    return CensoModel.findAll({
      where: origen ? { origen } : undefined,
      include: [{ association: 'censista', attributes: ['id', 'nombre'] }],
      order: [['id', 'ASC']]
    });
  }
}
