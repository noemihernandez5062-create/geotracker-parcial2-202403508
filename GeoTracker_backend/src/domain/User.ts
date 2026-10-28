export interface User {
  id?: number;
  nombre: string;
  correo: string;
  telefono?: string | null;
  passwordHash: string;
  activo?: boolean;
  fechaRegistro?: Date;
}
