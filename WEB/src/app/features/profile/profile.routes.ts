import {Routes} from '@angular/router';
import {authGuard} from '@core/guards/auth-guard';

export const PROFILE_ROUTES: Routes = [
	{
		path: '',
		canActivate: [authGuard],
		loadComponent: () =>
			import('./pages/profile-page/profile.component')
				.then(m => m.ProfileComponent)
	}
];
