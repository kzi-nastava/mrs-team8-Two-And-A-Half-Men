import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { inject } from '@angular/core';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export const loggedInGuard: CanActivateFn = () => {
	const authService = inject(AuthService);
	const router = inject(Router);
	console.log('LoggedInGuard invoked');
	if (!authService.isLoggedIn()) {
		console.log('User is not logged in, allowing access to unregistered routes');
		return true;
	}
	console.log(authService.user());
	switch (authService.user()?.role) {
		case LoggedInUserRole.ADMIN:
			console.log('User is admin, redirecting to admin dashboard');
			router.navigate(['admin']).then()
			break;
		case LoggedInUserRole.DRIVER:
			console.log('User is driver, redirecting to driver dashboard');
			router.navigate(['driver']).then()
			break;
		case LoggedInUserRole.CUSTOMER:
			console.log('User is customer, redirecting to home');
			router.navigate(['home']).then()
			break;
	}
	console.log('role not recognized, redirecting to home');
	return false;
};
