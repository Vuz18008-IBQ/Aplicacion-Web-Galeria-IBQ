import { Autor } from './autor';
import { Tecnica } from './tecnica';
import { Material } from './material';
import { Imagen } from './imagen';

export interface Obra {
  id: number;
  titulo: string;
  datacion: string | null;
  anio: number | null;
  dimensiones: string | null;
  tipologia: string | null;
  descripcion: string | null;
  marcas_inscripciones: string | null;
  referencias: string | null;
  fecha_ingreso: string | null;
  modo_ingreso: string | null;
  procedencia: string | null;
  estado_conservacion: string | null;
  restauraciones: string | null;
  ubicacion: string | null;
  observaciones: string | null;
  autor: Autor | null;
  tecnica: Tecnica | null;
  materiales: Material[] | null;
  imagenes: Imagen[] | null;
}

export interface ObraRequest {
  titulo: string;
  datacion: string | null;
  anio: number | null;
  dimensiones: string | null;
  tipologia: string | null;
  descripcion: string | null;
  marcas_inscripciones: string | null;
  referencias: string | null;
  fecha_ingreso: string | null;
  modo_ingreso: string | null;
  procedencia: string | null;
  estado_conservacion: string | null;
  restauraciones: string | null;
  ubicacion: string | null;
  observaciones: string | null;
  autorId: number;
  tecnicaId: number;
}
