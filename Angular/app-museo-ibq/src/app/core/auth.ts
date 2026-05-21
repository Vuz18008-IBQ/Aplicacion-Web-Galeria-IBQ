import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest, AuthResponse } from '../shared/models/auth';

const ACCESS_TOKEN_KEY = 'accessToken';
const REFRESH_TOKEN_KEY = 'refreshToken';

@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);
  private router = inject(Router);

  authenticated = signal<boolean>(this.hasToken());

  login(email: string, password: string): Observable<AuthResponse> {
    const body: LoginRequest = { email, password };
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/api/auth/login`, body)
      .pipe(tap(res => this.saveTokens(res)));
  }

  logout(): void {
    sessionStorage.removeItem(ACCESS_TOKEN_KEY);
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
    this.authenticated.set(false);
    this.router.navigate(['/admin/login']);
  }

  getAccessToken(): string | null {
    return sessionStorage.getItem(ACCESS_TOKEN_KEY);
  }

  private hasToken(): boolean {
    return !!sessionStorage.getItem(ACCESS_TOKEN_KEY);
  }

  private saveTokens(res: AuthResponse): void {
    sessionStorage.setItem(ACCESS_TOKEN_KEY, res.accessToken);
    sessionStorage.setItem(REFRESH_TOKEN_KEY, res.refreshToken);
    this.authenticated.set(true);
  }
}
