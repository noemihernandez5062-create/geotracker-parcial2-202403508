import {
  DataTypes,
  InferAttributes,
  InferCreationAttributes,
  Model,
  CreationOptional
} from 'sequelize';

import sequelize from '../database/sequelize';

// Resultado de cada verificacion biometrica (Face Liveness + comparacion con la foto de perfil)
export class VerificacionModel extends Model<
  InferAttributes<VerificacionModel>,
  InferCreationAttributes<VerificacionModel>
> {
  declare id: CreationOptional<number>;
  declare usuarioId: number;
  declare sessionId: string;
  declare status: string;
  declare confianza: number | null;
  declare esReal: boolean;
  declare coincide: boolean | null;
  declare similitud: number | null;
  declare fechaRegistro: CreationOptional<Date>;
}

VerificacionModel.init(
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
      field: 'Id'
    },

    usuarioId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      field: 'UsuarioId'
    },

    sessionId: {
      type: DataTypes.STRING(64),
      allowNull: false,
      field: 'SessionId'
    },

    // Estado de la sesion en Rekognition: CREATED | IN_PROGRESS | SUCCEEDED | FAILED | EXPIRED
    status: {
      type: DataTypes.STRING(20),
      allowNull: false,
      field: 'Status'
    },

    confianza: {
      type: DataTypes.DOUBLE,
      allowNull: true,
      field: 'Confianza'
    },

    esReal: {
      type: DataTypes.BOOLEAN,
      allowNull: false,
      field: 'EsReal'
    },

    // null si no se llego a comparar (persona no real o sin foto de perfil)
    coincide: {
      type: DataTypes.BOOLEAN,
      allowNull: true,
      field: 'Coincide'
    },

    similitud: {
      type: DataTypes.DOUBLE,
      allowNull: true,
      field: 'Similitud'
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
    tableName: 'Verificaciones',
    timestamps: false
  }
);

export default VerificacionModel;
