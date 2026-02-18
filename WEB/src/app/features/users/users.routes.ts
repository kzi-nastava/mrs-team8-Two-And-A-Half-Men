import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export const USERS_ROUTES: Routes = [
	{
		path: '',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('@features/users/pages/all-users-page/all-users-page.component').then(
				(m) => m.AllUsersPageComponent,
			),
	},
	{
		path: ':userId',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('@features/users/pages/user-details-page/user-details-page.component').then(
				(m) => m.UserDetailsPageComponent,
			),
	},
	{
		path: 'drivers/new',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('@features/users/pages/new-driver-page/new-driver-page.component').then(
				(m) => m.NewDriverPageComponent,
			),
	}
];
