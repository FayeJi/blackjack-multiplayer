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

            // Check if the 'roles' array includes 'ROLE_ADMIN'
            const isAdmin = decodedToken.roles.includes('ROLE_ADMIN');

            if (isAdmin) {
                return true; // Access granted
            }

        } catch (error) {
            console.error('Error decoding token', error);
            router.navigate(['/lobby']); // Invalid token, redirect
            return false;
        }
    }

    // If not an admin or no token, redirect to the lobby
    router.navigate(['/lobby']);
    return false;
};