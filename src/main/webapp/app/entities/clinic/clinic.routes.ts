import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ClinicResolve from './route/clinic-routing-resolve.service';

const clinicRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/clinic.component').then(m => m.ClinicComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/clinic-detail.component').then(m => m.ClinicDetailComponent),
    resolve: {
      clinic: ClinicResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/clinic-update.component').then(m => m.ClinicUpdateComponent),
    resolve: {
      clinic: ClinicResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/clinic-update.component').then(m => m.ClinicUpdateComponent),
    resolve: {
      clinic: ClinicResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default clinicRoute;
