import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Obra, ObraRequest } from '../shared/models/obra';

@Injectable({ providedIn: 'root' })
export class Obras {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/api/obras`;

  findAll(): Observable<Obra[]> {
    return this.http.get<Obra[]>(this.url);
  }

  findById(id: number): Observable<Obra> {
    return this.http.get<Obra>(`${this.url}/${id}`);
  }

  create(obra: ObraRequest): Observable<Obra> {
    return this.http.post<Obra>(this.url, obra);
  }

  update(id: number, obra: ObraRequest): Observable<Obra> {
    return this.http.put<Obra>(`${this.url}/${id}`, obra);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
