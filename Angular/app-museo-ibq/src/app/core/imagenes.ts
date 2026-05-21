import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Imagen } from '../shared/models/imagen';

@Injectable({ providedIn: 'root' })
export class Imagenes {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  upload(obraId: number, file: File, esPrincipal: boolean): Observable<Imagen> {
    const form = new FormData();
    form.append('file', file);
    form.append('esPrincipal', String(esPrincipal));
    return this.http.post<Imagen>(`${this.apiUrl}/api/obras/${obraId}/imagenes`, form);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/imagenes/${id}`);
  }

  fullUrl(imagen: Imagen): string {
    return `${this.apiUrl}${imagen.url}`;
  }
}
