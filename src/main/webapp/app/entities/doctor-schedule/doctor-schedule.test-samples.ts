import dayjs from 'dayjs/esm';

import { IDoctorSchedule, NewDoctorSchedule } from './doctor-schedule.model';

export const sampleWithRequiredData: IDoctorSchedule = {
  id: 8685,
  dayOfWeek: 6,
  startTime: dayjs('2026-01-28T20:26'),
  endTime: dayjs('2026-01-28T11:58'),
};

export const sampleWithPartialData: IDoctorSchedule = {
  id: 31915,
  dayOfWeek: 3,
  startTime: dayjs('2026-01-28T10:20'),
  endTime: dayjs('2026-01-28T14:09'),
};

export const sampleWithFullData: IDoctorSchedule = {
  id: 25475,
  dayOfWeek: 4,
  startTime: dayjs('2026-01-28T12:52'),
  endTime: dayjs('2026-01-28T15:23'),
};

export const sampleWithNewData: NewDoctorSchedule = {
  dayOfWeek: 4,
  startTime: dayjs('2026-01-28T21:03'),
  endTime: dayjs('2026-01-28T12:18'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
