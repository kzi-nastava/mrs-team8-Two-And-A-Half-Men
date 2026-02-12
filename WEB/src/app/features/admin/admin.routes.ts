import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export const ADMIN_ROUTES: Routes = [
	{
		path: '',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('./home/pages/admin-home-page/admin-home-page.component').then(
				(m) => m.AdminHomePageComponent,
			),
	}
];
