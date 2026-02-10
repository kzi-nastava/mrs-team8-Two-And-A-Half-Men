import {Routes} from '@angular/router';
import {authGuard} from '@core/guards/auth-guard';

export const REPORTS_ROUTES: Routes = [
	{
		path: '',
		canActivate: [authGuard],
		loadComponent: () =>
			import('./pages/report-page/report-page.component')
				.then(m => m.ReportPageComponent)
	}
];
