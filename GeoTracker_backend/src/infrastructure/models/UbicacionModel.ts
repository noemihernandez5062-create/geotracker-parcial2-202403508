import {
  DataTypes,
  InferAttributes,
  InferCreationAttributes,
  Model,
  CreationOptional,
  ForeignKey
} from 'sequelize';

import sequelize from '../database/sequelize';
import UsuarioModel from './UsuarioModel';

// Ubicaciones de interes capturadas desde la app GeoTracker
export class UbicacionModel extends Model<
  InferAttributes<UbicacionModel>,
  InferCreationAttributes<UbicacionModel>
> {
  declare id: CreationOptional<number>;
  declare nombre: string;
  declare descripcion: string;
  declare latitud: number;
  declare longitud: number;
  declare usuarioId: ForeignKey<number>;
  declare fechaCreacion: CreationOptional<Date>;
}

UbicacionModel.init(
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
      field: 'Id'
    },

    nombre: {
      type: DataTypes.STRING(100),
      allowNull: false,
      field: 'Nombre'
    },

    descripcion: {
      type: DataTypes.STRING(500),
      allowNull: false,
      field: 'Descripcion'
    },

    latitud: {
      type: DataTypes.DOUBLE,
      allowNull: false,
      field: 'Latitud'
    },

    longitud: {
      type: DataTypes.DOUBLE,
      allowNull: false,
      field: 'Longitud'
    },

    // Usuario dueño de la ubicacion
    usuarioId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      field: 'UsuarioId'
    },

    fechaCreacion: {
      type: DataTypes.DATE,
      allowNull: false,
      defaultValue: DataTypes.NOW,
      field: 'FechaCreacion'
    }
  },
  {
    sequelize,
    tableName: 'Ubicaciones',
    timestamps: false
  }
);

UbicacionModel.belongsTo(UsuarioModel, {
  as: 'usuario',
  foreignKey: 'usuarioId',
  onDelete: 'CASCADE'
});

export default UbicacionModel;
