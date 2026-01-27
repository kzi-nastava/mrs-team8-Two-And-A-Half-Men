import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '@core/services/auth-service.service';

// Factory function to create a guard for specific roles
export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
	return () => {
		const auth = inject(AuthService);
		const router = inject(Router);
		const user = auth.user(); // returns { id, roles: string[] }
		if (!user) {
			// not logged in, redirect to login page
			return router.createUrlTree(['/login']);
		}

		const hasRole = allowedRoles.includes(user.role);
		if (hasRole) return true;

		switch (user.role) {
			case 'ADMIN':
				return router.createUrlTree(['/admin']);
			case 'DRIVER':
				return router.createUrlTree(['/driver']);
			case 'USER':
				return router.createUrlTree(['/home']);
		}
		// redirect to
		return router.createUrlTree(['/']);
	};
};
