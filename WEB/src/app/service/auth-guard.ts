import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { Auth } from './auth';

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const authService = inject(Auth);

  const userRole = authService.getRole();
  console.log('User Role:', userRole); 
  if (userRole == null) {
    router.navigate(['login']);
    return false;
  }

  if (!route.data['role'].includes(userRole)) {
    router.navigate(['home']);
    return false;
  }

  return true;
};