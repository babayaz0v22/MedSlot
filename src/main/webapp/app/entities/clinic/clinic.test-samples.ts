import dayjs from 'dayjs/esm';

import { IClinic, NewClinic } from './clinic.model';

export const sampleWithRequiredData: IClinic = {
  id: 26889,
  name: 'outside hourly inside',
  active: false,
  createdBy: 'questionably till',
};

export const sampleWithPartialData: IClinic = {
  id: 23069,
  name: 'fall outside wealthy',
  active: true,
  createdBy: 'rationale',
};

export const sampleWithFullData: IClinic = {
  id: 25972,
  name: 'license but',
  phone: '(931)893-18-38',
  address: 'doubtfully bah until',
  active: false,
  createdBy: 'solemnly until',
  createdDate: dayjs('2026-01-28T15:25'),
  lastModifiedBy: 'aw even',
  lastModifiedDate: dayjs('2026-01-29T01:20'),
};

export const sampleWithNewData: NewClinic = {
  name: 'hence makeover how',
  active: true,
  createdBy: 'yowza',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
