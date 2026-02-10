import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';

export const HISTORY_ROUTES: Routes = [
	{
		path: '',
		canActivate: [authGuard],
		loadComponent: () =>
			import('@features/history/components/history/history.component').then((m) => m.HistoryComponent),
	},
];
