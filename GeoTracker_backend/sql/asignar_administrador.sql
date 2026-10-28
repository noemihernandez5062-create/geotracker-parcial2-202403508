-- Convierte un usuario ya registrado en administrador.
-- Todos los usuarios se registran como CENSISTA; el rol de administrador solo
-- se asigna desde la base de datos.
--
-- Cambia el correo y ejecuta. El usuario debe volver a iniciar sesion para que
-- su token incluya el nuevo rol.

UPDATE "Usuarios"
SET "Rol" = 'ADMINISTRADOR'
WHERE "Correo" = 'admin@gmail.com';
