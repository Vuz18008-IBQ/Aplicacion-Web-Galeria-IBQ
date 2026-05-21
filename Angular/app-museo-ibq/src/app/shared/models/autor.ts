export interface Autor {
  id: number;
  nombre: string;
  apellidos: string;
  fecha_nacimiento: number | null;
  fecha_muerte: number | null;
  corriente_artistica: string | null;
  lugar_nacimiento: string | null;
  estado_borrado?: boolean;
}
