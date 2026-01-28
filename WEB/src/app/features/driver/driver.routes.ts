import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export const DRIVER_ROUTES: Routes = [
	{
		path: '',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.DRIVER])],
		loadComponent: () =>
			import('@features/driver/home/pages/driver-home-page/driver-home-page.component').then(
				(m) => m.DriverHomePageComponent,
			),
	},
	{
		path: 'history',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.DRIVER])],
		loadComponent: () =>
			import('./history/pages/history-page/history-page.component').then(
				(m) => m.DriversHistoryComponent,
			),
	},
	{
		path: 'rides/active',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.DRIVER])],
		loadComponent: () =>
			import('./rides/pages/active-ride-page/active-ride-page.component').then(
				(m) => m.ActiveRidePageComponent,
			),
	},
	{
		path: 'rides/:rideId',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.DRIVER])],
		loadComponent: () =>
			import('./rides/pages/ride-details-page/ride-details-page.component').then(
				(m) => m.RideDetailsPageComponent,
			),
	},
];
