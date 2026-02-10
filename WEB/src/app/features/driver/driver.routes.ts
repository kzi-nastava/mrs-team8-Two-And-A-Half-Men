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
	},
	{
		path: 'history',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.DRIVER])],
		loadComponent: () =>
			import('@features/history/components/history/history.component').then(
				(m) => m.HistoryComponent,
			),
		data: { userRole: LoggedInUserRole.DRIVER },
	},
	{
		path: 'history/:id',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.DRIVER])],
		loadComponent: () =>
			import('@features/history/components/ride-details/ride-details.component').then(
				(m) => m.RideDetailsComponent,
			),
		data: { userRole: LoggedInUserRole.DRIVER },
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
