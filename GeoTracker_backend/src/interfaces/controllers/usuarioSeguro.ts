// Datos del usuario que se pueden enviar a la app (sin password ni foto)
export function usuarioSeguro(usuario: any) {
  return {
    id: usuario.id,
    nombre: usuario.nombre,
    correo: usuario.correo,
    telefono: usuario.telefono,
    rol: usuario.rol,
    tieneFoto: Boolean(usuario.fotoTipo),
    fechaRegistro: usuario.fechaRegistro,
    activo: usuario.activo
  };
}
