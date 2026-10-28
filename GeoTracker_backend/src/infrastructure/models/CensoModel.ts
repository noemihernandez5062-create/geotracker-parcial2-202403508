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

// NORMAL = capturado desde la app, MASIVO = insertado con el script de BD
export const ORIGENES = ['NORMAL', 'MASIVO'] as const;
export type Origen = typeof ORIGENES[number];

export class CensoModel extends Model<
  InferAttributes<CensoModel>,
  InferCreationAttributes<CensoModel>
> {
  declare id: CreationOptional<number>;
  declare nombre: string;
  declare apellido: string;
  declare totalMiembros: number;
  declare cantidadTelevisiones: number;
  declare latitud: number;
  declare longitud: number;
  declare origen: CreationOptional<Origen>;
  declare usuarioId: ForeignKey<number> | null;
  declare fechaRegistro: CreationOptional<Date>;
}

CensoModel.init(
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

    apellido: {
      type: DataTypes.STRING(100),
      allowNull: false,
      field: 'Apellido'
    },

    totalMiembros: {
      type: DataTypes.INTEGER,
      allowNull: false,
      field: 'TotalMiembros'
    },

    cantidadTelevisiones: {
      type: DataTypes.INTEGER,
      allowNull: false,
      field: 'CantidadTelevisiones'
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

    origen: {
      type: DataTypes.STRING(10),
      allowNull: false,
      defaultValue: 'NORMAL',
      field: 'Origen'
    },

    // Censista que lo capturo. Es null en los censos masivos.
    usuarioId: {
      type: DataTypes.INTEGER,
      allowNull: true,
      field: 'UsuarioId'
    },

    fechaRegistro: {
      type: DataTypes.DATE,
      allowNull: false,
      defaultValue: DataTypes.NOW,
      field: 'FechaRegistro'
    }
  },
  {
    sequelize,
    tableName: 'Censos',
    timestamps: false
  }
);

CensoModel.belongsTo(UsuarioModel, {
  as: 'censista',
  foreignKey: 'usuarioId'
});

export default CensoModel;
