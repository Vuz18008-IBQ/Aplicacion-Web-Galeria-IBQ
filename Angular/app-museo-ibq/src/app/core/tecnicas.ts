import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Tecnica } from '../shared/models/tecnica';

@Injectable({ providedIn: 'root' })
export class Tecnicas {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/api/tecnicas`;

  findAll(): Observable<Tecnica[]> {
    return this.http.get<Tecnica[]>(this.url);
  }
}
