import dayjs from 'dayjs/esm';

import { IAppointment, NewAppointment } from './appointment.model';

export const sampleWithRequiredData: IAppointment = {
  id: 13399,
  appointmentDateTime: dayjs('2026-01-29T00:20'),
  status: 'BOOKED',
};

export const sampleWithPartialData: IAppointment = {
  id: 19136,
  appointmentDateTime: dayjs('2026-01-28T17:14'),
  status: 'CANCELED',
};

export const sampleWithFullData: IAppointment = {
  id: 5963,
  appointmentDateTime: dayjs('2026-01-28T16:39'),
  status: 'DONE',
  note: 'exhaust ah',
};

export const sampleWithNewData: NewAppointment = {
  appointmentDateTime: dayjs('2026-01-29T04:59'),
  status: 'DONE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
