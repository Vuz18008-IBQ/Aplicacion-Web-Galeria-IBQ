import { Routes } from '@angular/router';
import { authGuard } from '../core/auth-guard';

export const adminRoutes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./login/login').then(m => m.Login)
  },
  {
    path: 'obras',
    canMatch: [authGuard],
    loadComponent: () => import('./obras-list/obras-list').then(m => m.ObrasList)
  },
  {
    path: 'obras/nueva',
    canMatch: [authGuard],
    loadComponent: () => import('./obra-form/obra-form').then(m => m.ObraForm)
  },
  {
    path: 'obras/:id/editar',
    canMatch: [authGuard],
    loadComponent: () => import('./obra-form/obra-form').then(m => m.ObraForm)
  },
  { path: '', redirectTo: 'obras', pathMatch: 'full' }
];
