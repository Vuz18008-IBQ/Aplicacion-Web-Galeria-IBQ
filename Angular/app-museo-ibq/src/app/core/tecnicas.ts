import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Tecnica, TecnicaRequest } from '../shared/models/tecnica';

@Injectable({ providedIn: 'root' })
export class Tecnicas {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/api/tecnicas`;

  findAll(): Observable<Tecnica[]> {
    return this.http.get<Tecnica[]>(this.url);
  }

  findById(id: number): Observable<Tecnica> {
    return this.http.get<Tecnica>(`${this.url}/${id}`);
  }

  create(tecnica: TecnicaRequest): Observable<Tecnica> {
    return this.http.post<Tecnica>(this.url, tecnica);
  }

  update(id: number, tecnica: TecnicaRequest): Observable<Tecnica> {
    return this.http.put<Tecnica>(`${this.url}/${id}`, tecnica);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
