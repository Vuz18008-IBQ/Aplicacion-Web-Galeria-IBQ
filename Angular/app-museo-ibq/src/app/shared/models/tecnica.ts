export interface Tecnica {
  id: number;
  nombre: string;
  estado_borrado?: boolean;
}

export interface TecnicaRequest {
  nombre: string;
}
