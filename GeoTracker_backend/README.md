# BACKEND_MOVIL (version PostgreSQL)

Backend Node.js + TypeScript + Express + Sequelize + PostgreSQL para una app Android con Registro, Login y Perfil.

Esta version usa PostgreSQL en lugar de SQL Server, para poder correr en un servidor Linux pequeno (AWS EC2).

## 1. Base de datos
Crea una base de datos vacia en PostgreSQL:

```bash
psql -U postgres -c "CREATE DATABASE bd_movil;"
```

La tabla `Usuarios` se crea sola al iniciar el servidor (`sequelize.sync()`).

## 2. Configuracion
Copia `.env.example` como `.env` y ajusta:

- DB_HOST
- DB_PORT (normalmente 5432)
- DB_NAME
- DB_USER
- DB_PASSWORD
- PORT
- JWT_SECRET

## 3. Dependencias

```bash
npm install
```

## 4. Ejecutar

Desarrollo:

```bash
npm run dev
```

Produccion:

```bash
npm run build
npm start
```

## Endpoints

### Registro
POST `/api/auth/register`

```json
{
  "nombre": "Ana",
  "correo": "ana@gmail.com",
  "telefono": "55555555",
  "password": "12345"
}
```

### Login
POST `/api/auth/login`

```json
{
  "correo": "ana@gmail.com",
  "password": "12345"
}
```

Guarda el `token` de la respuesta (`data.token`).

### Perfil
GET `/api/profile`

Header:
`Authorization: Bearer TU_TOKEN`

### Actualizar perfil
PUT `/api/profile`

Header:
`Authorization: Bearer TU_TOKEN`

```json
{
  "nombre": "Ana Hernandez",
  "telefono": "55550000"
}
```


### Foto de perfil
PUT `/api/profile/foto`: el cuerpo es la imagen en bytes (no JSON).

Headers:
- `Authorization: Bearer TU_TOKEN`
- `Content-Type: image/jpeg` (o `image/png`, `image/webp`). Maximo 5 MB.

GET `/api/profile/foto`: devuelve la imagen en bytes. `404` si el usuario no tiene foto.
El perfil incluye `tieneFoto: true/false`.

## Roles
- `CENSISTA`: rol por defecto al registrarse. Puede capturar censos.
- `ADMINISTRADOR`: puede ver los censos en el mapa. Se asigna desde la BD con
  `sql/asignar_administrador.sql`; el usuario debe volver a iniciar sesion.

El login devuelve `data.usuario.rol`. Una ruta sin permiso responde `403`.

## Censos
### Capturar censo (solo CENSISTA)
POST `/api/censos`

```json
{
  "nombre": "Juan",
  "apellido": "Perez",
  "totalMiembros": 4,
  "cantidadTelevisiones": 2,
  "latitud": 14.6,
  "longitud": -90.5
}
```

`totalMiembros` entero >= 1, `cantidadTelevisiones` entero >= 0. Se guarda con `origen: "NORMAL"`.

### Ver censos (solo ADMINISTRADOR)
GET `/api/censos`: todos.
GET `/api/censos?origen=NORMAL` o `?origen=MASIVO`: filtrados.

Cada censo: `{ id, nombre, apellido, totalMiembros, cantidadTelevisiones, latitud, longitud, origen, censista, fechaRegistro }`.

### Carga masiva
`sql/censos_masivos.sql` crea la funcion PL/pgSQL `insertar_censos_masivos(n)`, que
inserta `n` censos aleatorios con `origen = 'MASIVO'` (zona por defecto: Ciudad de Guatemala).

Desde el servidor (necesita `psql` y el `.env`):

```bash
bash scripts/cargar_censos.sh 1000
```

O directo en la BD:

```sql
SELECT insertar_censos_masivos(1000);
```

## Ubicaciones (app GeoTracker)
Cualquier usuario autenticado. Header: `Authorization: Bearer TU_TOKEN`.

### Guardar ubicacion
POST `/api/ubicaciones`

```json
{
  "nombre": "Parque central",
  "descripcion": "Frente a la catedral",
  "latitud": 14.6425,
  "longitud": -90.5133
}
```

Exito: `201`. Errores: `400` datos invalidos, `401` sin token o token vencido.

### Ver mis ubicaciones
GET `/api/ubicaciones`: solo las del usuario del token, la mas reciente primero.

Cada ubicacion: `{ id, nombre, descripcion, latitud, longitud, fechaCreacion }`.

Tabla `Ubicaciones`: `Id`, `UsuarioId` (FK a `Usuarios`), `Nombre`, `Descripcion`,
`Latitud`, `Longitud`, `FechaCreacion`. Se crea sola al iniciar (`sequelize.sync()`).

## Actualizar el servidor
Al iniciar, el backend crea la tabla `Censos` y agrega a `Usuarios` las columnas
`Rol`, `Foto` y `FotoTipo` sin borrar datos (`src/infrastructure/database/migraciones.ts`).

## Respuesta estandar

Todas las rutas devuelven:

```json
{
  "success": true,
  "message": "...",
  "data": {}
}
```

## Android
- Emulador contra el backend en tu PC: `http://10.0.2.2:PORT/`
- Backend desplegado en AWS: `http://3.134.144.181:3001/`
