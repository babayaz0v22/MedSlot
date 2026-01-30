import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DoctorScheduleResolve from './route/doctor-schedule-routing-resolve.service';

const doctorScheduleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/doctor-schedule.component').then(m => m.DoctorScheduleComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/doctor-schedule-detail.component').then(m => m.DoctorScheduleDetailComponent),
    resolve: {
      doctorSchedule: DoctorScheduleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/doctor-schedule-update.component').then(m => m.DoctorScheduleUpdateComponent),
    resolve: {
      doctorSchedule: DoctorScheduleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/doctor-schedule-update.component').then(m => m.DoctorScheduleUpdateComponent),
    resolve: {
      doctorSchedule: DoctorScheduleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default doctorScheduleRoute;
