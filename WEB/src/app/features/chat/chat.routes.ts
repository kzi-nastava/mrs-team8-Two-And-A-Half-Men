import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export const CHAT_ROUTES: Routes = [
	{
		path: '',
		loadComponent: () =>
			import('@features/chat/pages/admin-chats-page/admin-chats-page.component').then(
				(m) => m.AdminChatsPageComponent,
			),
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		data: { roles: ['ADMIN'] },
	},
];
