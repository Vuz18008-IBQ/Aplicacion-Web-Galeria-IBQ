import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Autor, AutorRequest } from '../shared/models/autor';

@Injectable({ providedIn: 'root' })
export class Autores {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/api/autores`;

  findAll(): Observable<Autor[]> {
    return this.http.get<Autor[]>(this.url);
  }

  findById(id: number): Observable<Autor> {
    return this.http.get<Autor>(`${this.url}/${id}`);
  }

  create(autor: AutorRequest): Observable<Autor> {
    return this.http.post<Autor>(this.url, autor);
  }

  update(id: number, autor: AutorRequest): Observable<Autor> {
    return this.http.put<Autor>(`${this.url}/${id}`, autor);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
