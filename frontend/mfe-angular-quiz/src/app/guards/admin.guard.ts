import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const adminGuard: CanActivateFn = (route, state) => {
    const router = inject(Router);
    const userStr = localStorage.getItem('pilotquiz_user');

    if (userStr) {
        try {
            const user = JSON.parse(userStr);
            if (user.role === 'ROLE_ADMIN') {
                return true;
            }
        } catch (e) {
            // Invalid JSON
        }
    }

    // Redirect to home or login if not admin
    router.navigate(['/']);
    return false;
};
