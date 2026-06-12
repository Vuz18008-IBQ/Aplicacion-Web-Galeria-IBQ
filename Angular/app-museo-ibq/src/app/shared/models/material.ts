export interface Material {
  id: number;
  nombre: string;
  estado_borrado?: boolean;
}

export interface MaterialRequest {
  nombre: string;
}
