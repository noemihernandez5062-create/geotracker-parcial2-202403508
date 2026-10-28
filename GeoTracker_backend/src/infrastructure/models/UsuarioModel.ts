import {
  DataTypes,
  InferAttributes,
  InferCreationAttributes,
  Model,
  CreationOptional
} from 'sequelize';

import sequelize from '../database/sequelize';

export const ROLES = ['CENSISTA', 'ADMINISTRADOR'] as const;
export type Rol = typeof ROLES[number];

export class UsuarioModel extends Model<
  InferAttributes<UsuarioModel>,
  InferCreationAttributes<UsuarioModel>
> {
  declare id: CreationOptional<number>;
  declare nombre: string;
  declare correo: string;
  declare telefono: string | null;
  declare passwordHash: string;
  declare rol: CreationOptional<Rol>;
  declare foto: CreationOptional<Buffer | null>;
  declare fotoTipo: CreationOptional<string | null>;
  declare fechaRegistro: CreationOptional<Date>;
  declare activo: CreationOptional<boolean>;
}

UsuarioModel.init(
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

    correo: {
      type: DataTypes.STRING(150),
      allowNull: false,
      unique: true,
      field: 'Correo'
    },

    telefono: {
      type: DataTypes.STRING(20),
      allowNull: true,
      field: 'Telefono'
    },

    passwordHash: {
      type: DataTypes.STRING(255),
      allowNull: false,
      field: 'PasswordHash'
    },

    rol: {
      type: DataTypes.STRING(20),
      allowNull: false,
      defaultValue: 'CENSISTA',
      field: 'Rol'
    },

    // Foto de perfil guardada como bytes (BYTEA en PostgreSQL)
    foto: {
      type: DataTypes.BLOB,
      allowNull: true,
      field: 'Foto'
    },

    fotoTipo: {
      type: DataTypes.STRING(50),
      allowNull: true,
      field: 'FotoTipo'
    },

    fechaRegistro: {
      type: DataTypes.DATE,
      allowNull: false,
      defaultValue: DataTypes.NOW,
      field: 'FechaRegistro'
    },

    activo: {
      type: DataTypes.BOOLEAN,
      allowNull: false,
      defaultValue: true,
      field: 'Activo'
    }
  },
  {
    sequelize,
    tableName: 'Usuarios',
    timestamps: false,

    // La foto no se carga en cada consulta; solo con el scope 'conFoto'
    defaultScope: {
      attributes: { exclude: ['foto'] }
    },
    scopes: {
      conFoto: {}
    }
  }
);

export default UsuarioModel;
