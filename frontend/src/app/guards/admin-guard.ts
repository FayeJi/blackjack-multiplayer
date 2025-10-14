import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';
import { jwtDecode } from 'jwt-decode';

export const adminGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const token = authService.getToken();

    if (token) {
        try {
            const decodedToken: { sub: string, roles: string[] } = jwtDecode(token);
            const isAdmin = decodedToken.roles.includes('ROLE_ADMIN');

            if (isAdmin) {
                return true; 
            }

        } catch (error) {
            console.error('Error decoding token', error);
            router.navigate(['/lobby']);
            return false;
        }
    }

    router.navigate(['/lobby']);
    return false;
};