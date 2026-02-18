import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '@core/services/auth.service';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

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

		console.log('role guard');
		const hasRole = allowedRoles.includes(user.role);
		if (hasRole) return true;

		switch (user.role) {
			case LoggedInUserRole.ADMIN:
				return router.createUrlTree(['/admin']);
			case LoggedInUserRole.DRIVER:
				return router.createUrlTree(['/driver']);
			case LoggedInUserRole.CUSTOMER:
				return router.createUrlTree(['/home']);
		}
	};
};
