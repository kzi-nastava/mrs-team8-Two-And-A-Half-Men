import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';

export const DRIVER_ROUTES: Routes = [
	{
		path: 'rides/:rideId',
		canActivate: [authGuard, roleGuard(['DRIVER'])],
		loadComponent: () =>
			import('./rides/pages/ride-details-page/ride-details-page.component').then(
				(m) => m.RideDetailsPageComponent,
			),
	},
];
