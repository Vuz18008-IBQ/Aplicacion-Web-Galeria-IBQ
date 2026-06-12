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
  {
    path: 'autores',
    canMatch: [authGuard],
    loadComponent: () => import('./autores-list/autores-list').then(m => m.AutoresList)
  },
  {
    path: 'autores/nueva',
    canMatch: [authGuard],
    loadComponent: () => import('./autor-form/autor-form').then(m => m.AutorForm)
  },
  {
    path: 'autores/:id/editar',
    canMatch: [authGuard],
    loadComponent: () => import('./autor-form/autor-form').then(m => m.AutorForm)
  },
  {
    path: 'tecnicas',
    canMatch: [authGuard],
    loadComponent: () => import('./tecnicas-list/tecnicas-list').then(m => m.TecnicasList)
  },
  {
    path: 'tecnicas/nueva',
    canMatch: [authGuard],
    loadComponent: () => import('./tecnica-form/tecnica-form').then(m => m.TecnicaForm)
  },
  {
    path: 'tecnicas/:id/editar',
    canMatch: [authGuard],
    loadComponent: () => import('./tecnica-form/tecnica-form').then(m => m.TecnicaForm)
  },
  {
    path: 'materiales',
    canMatch: [authGuard],
    loadComponent: () => import('./materiales-list/materiales-list').then(m => m.MaterialesList)
  },
  {
    path: 'materiales/nuevo',
    canMatch: [authGuard],
    loadComponent: () => import('./material-form/material-form').then(m => m.MaterialForm)
  },
  {
    path: 'materiales/:id/editar',
    canMatch: [authGuard],
    loadComponent: () => import('./material-form/material-form').then(m => m.MaterialForm)
  },
  { path: '', redirectTo: 'obras', pathMatch: 'full' }
];
