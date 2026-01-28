import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export const customerRoutes: Routes = [
	{
		path: 'home',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.CUSTOMER])],
		loadComponent: () =>
			import('./home/pages/customer-home-page/customer-home-page.component').then(
				(m) => m.CustomerHomePageComponent,
			),
	},
	{
		path: 'rides/booked',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.CUSTOMER])],
		loadComponent: () =>
			import('./rides/pages/booked-rides-page/booked-rides-page').then((m) => m.BookedRides),
	},
];
