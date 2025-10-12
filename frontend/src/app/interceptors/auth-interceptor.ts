import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService); // Use inject() to get service instances
    const token = authService.getToken();

    if (token) {
        // Clone the request to add the new header, as requests are immutable
        const cloned = req.clone({
            headers: req.headers.set('Authorization', `Bearer ${token}`)
        });
        return next(cloned);
    }

    return next(req);
};