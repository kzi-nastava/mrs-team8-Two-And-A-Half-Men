import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';

export const RIDES_ROUTES: Routes = [
	{
		path: ':id',
		loadComponent: () =>
			import('@features/rides/pages/ride-details-page/ride-details-page.component').then(
				(m) => m.RideDetailsComponent,
			),
	},
];
