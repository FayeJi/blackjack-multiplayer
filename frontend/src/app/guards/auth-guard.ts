import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if the user is logged in
  if (authService.isLoggedIn()) {
    return true; // Access granted
  }

  // If not logged in, redirect to the login page
  router.navigate(['/login']);
  return false; // Access denied
};