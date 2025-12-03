import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../Services/auth.service';

/**
 * Guest Guard - Previene que usuarios autenticados accedan a páginas de login/register
 * Si el usuario ya está autenticado, lo redirige a /campaigns
 */

export const guestGuard = () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isAuthenticated()) {
        router.navigate(['/campaigns']);
        return false;
    }
    return true;
};
