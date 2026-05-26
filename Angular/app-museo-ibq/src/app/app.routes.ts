import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./public/galeria/galeria').then(m => m.Galeria)
  },
  {
    path: 'obra/:id',
    loadComponent: () => import('./public/obra-detalle/obra-detalle').then(m => m.ObraDetalle)
  },
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.routes').then(m => m.adminRoutes)
  },
  { path: '**', redirectTo: '' }
];
