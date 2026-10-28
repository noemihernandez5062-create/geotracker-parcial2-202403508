import 'dotenv/config';
import app from './src/app';
import sequelize from './src/infrastructure/database/sequelize';
import { ejecutarMigraciones } from './src/infrastructure/database/migraciones';
import './src/infrastructure/models/UsuarioModel';
import './src/infrastructure/models/CensoModel';
import './src/infrastructure/models/VerificacionModel';
import './src/infrastructure/models/UbicacionModel';

const PORT = Number(process.env.PORT || 3000);

async function startServer() {
  try {
    await sequelize.authenticate();
    console.log('Conexion a PostgreSQL establecida correctamente.');

    // Crea las tablas si no existen. No elimina datos existentes.
    await sequelize.sync();

    // Agrega columnas nuevas a tablas que ya existian.
    await ejecutarMigraciones();

    app.listen(PORT, '0.0.0.0', () => {
      console.log(`Servidor corriendo en http://localhost:${PORT}`);
    });
  } catch (error) {
    console.error('No fue posible iniciar el servidor:', error);
    process.exit(1);
  }
}

startServer();
