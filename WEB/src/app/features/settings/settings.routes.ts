import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export const SETTINGS_ROUTES: Routes = [
	{
		path: '',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('@features/settings/pages/settings-page/settings-page.component').then(
				(m) => m.SettingsPageComponent,
			),
	},
];
