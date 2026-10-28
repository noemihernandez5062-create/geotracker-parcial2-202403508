import 'dotenv/config';
import { Sequelize } from 'sequelize';

const host = process.env.DB_HOST || 'localhost';
const port = Number(process.env.DB_PORT || 5432);
const database = process.env.DB_NAME || 'bd_movil';
const username = process.env.DB_USER || '';
const password = process.env.DB_PASSWORD || '';

const sequelize = new Sequelize(database, username, password, {
  host,
  port,
  dialect: 'postgres',
  logging: false
});

export default sequelize;
