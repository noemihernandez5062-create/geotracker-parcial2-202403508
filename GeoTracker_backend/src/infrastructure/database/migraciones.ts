  import sequelize from './sequelize';

// sequelize.sync() crea tablas nuevas pero NO agrega columnas a tablas que ya
// existen. Estas sentencias agregan a "Usuarios" las columnas nuevas sin
// borrar datos; se pueden ejecutar cuantas veces sea.
export async function ejecutarMigraciones() {
  await sequelize.query(`
    ALTER TABLE "Usuarios"
      ADD COLUMN IF NOT EXISTS "Rol" VARCHAR(20) NOT NULL DEFAULT 'CENSISTA',
      ADD COLUMN IF NOT EXISTS "Foto" BYTEA NULL,
      ADD COLUMN IF NOT EXISTS "FotoTipo" VARCHAR(50) NULL;
  `);
}
