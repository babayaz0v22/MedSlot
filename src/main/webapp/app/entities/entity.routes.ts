import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'medSlotApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'clinic',
    data: { pageTitle: 'medSlotApp.clinic.home.title' },
    loadChildren: () => import('./clinic/clinic.routes'),
  },
  {
    path: 'doctor',
    data: { pageTitle: 'medSlotApp.doctor.home.title' },
    loadChildren: () => import('./doctor/doctor.routes'),
  },
  {
    path: 'patient',
    data: { pageTitle: 'medSlotApp.patient.home.title' },
    loadChildren: () => import('./patient/patient.routes'),
  },
  {
    path: 'doctor-schedule',
    data: { pageTitle: 'medSlotApp.doctorSchedule.home.title' },
    loadChildren: () => import('./doctor-schedule/doctor-schedule.routes'),
  },
  {
    path: 'appointment',
    data: { pageTitle: 'medSlotApp.appointment.home.title' },
    loadChildren: () => import('./appointment/appointment.routes'),
  },
  {
    path: 'sms-notification',
    data: { pageTitle: 'medSlotApp.smsNotification.home.title' },
    loadChildren: () => import('./sms-notification/sms-notification.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
