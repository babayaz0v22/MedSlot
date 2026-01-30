import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SmsNotificationResolve from './route/sms-notification-routing-resolve.service';

const smsNotificationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sms-notification.component').then(m => m.SmsNotificationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sms-notification-detail.component').then(m => m.SmsNotificationDetailComponent),
    resolve: {
      smsNotification: SmsNotificationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sms-notification-update.component').then(m => m.SmsNotificationUpdateComponent),
    resolve: {
      smsNotification: SmsNotificationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sms-notification-update.component').then(m => m.SmsNotificationUpdateComponent),
    resolve: {
      smsNotification: SmsNotificationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default smsNotificationRoute;
