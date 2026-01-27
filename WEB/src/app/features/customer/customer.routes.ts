import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';

export const customerRoutes: Routes = [
  {
    path: 'rides/booked',
    loadComponent: () => import('./rides/pages/booked-rides-page/booked-rides-page').
    then(m => m.BookedRides),
  }
];