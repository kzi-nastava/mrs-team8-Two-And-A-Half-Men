import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export const DRIVER_ROUTES: Routes = [
	{
		path: '',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.DRIVER])],
		loadComponent: () =>
			import('./home/pages/driver-home-page/driver-home-page.component').then(
				(m) => m.DriverHomePageComponent,
			),
	}
];
