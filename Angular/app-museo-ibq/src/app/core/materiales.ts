import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Material, MaterialRequest } from '../shared/models/material';

@Injectable({ providedIn: 'root' })
export class Materiales {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/api/materiales`;

  findAll(): Observable<Material[]> {
    return this.http.get<Material[]>(this.url);
  }

  findById(id: number): Observable<Material> {
    return this.http.get<Material>(`${this.url}/${id}`);
  }

  create(material: MaterialRequest): Observable<Material> {
    return this.http.post<Material>(this.url, material);
  }

  update(id: number, material: MaterialRequest): Observable<Material> {
    return this.http.put<Material>(`${this.url}/${id}`, material);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
