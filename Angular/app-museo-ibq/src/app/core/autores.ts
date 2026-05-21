import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Autor } from '../shared/models/autor';

@Injectable({ providedIn: 'root' })
export class Autores {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/api/autores`;

  findAll(): Observable<Autor[]> {
    return this.http.get<Autor[]>(this.url);
  }
}
