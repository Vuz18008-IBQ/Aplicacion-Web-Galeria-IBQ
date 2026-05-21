import { CanMatchFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { Auth } from './auth';

export const authGuard: CanMatchFn = () => {
  const auth = inject(Auth);
  const router = inject(Router);

  if (auth.authenticated()) {
    return true;
  }
  router.navigate(['/admin/login']);
  return false;
};
