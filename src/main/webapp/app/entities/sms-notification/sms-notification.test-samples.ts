import dayjs from 'dayjs/esm';

import { ISmsNotification, NewSmsNotification } from './sms-notification.model';

export const sampleWithRequiredData: ISmsNotification = {
  id: 28145,
  phone: '(916)374-44-74',
  message: 'until neatly yum',
  sendAt: dayjs('2026-01-28T10:48'),
  status: 'PENDING',
};

export const sampleWithPartialData: ISmsNotification = {
  id: 16571,
  phone: '(990)699-89-89',
  message: 'ah',
  sendAt: dayjs('2026-01-28T16:39'),
  sentAt: dayjs('2026-01-29T03:22'),
  status: 'PENDING',
  errorMessage: 'above aha',
};

export const sampleWithFullData: ISmsNotification = {
  id: 10671,
  phone: '(927)462-55-63',
  message: 'thankfully',
  sendAt: dayjs('2026-01-29T03:23'),
  sentAt: dayjs('2026-01-29T09:44'),
  status: 'FAILED',
  errorMessage: 'so boo',
};

export const sampleWithNewData: NewSmsNotification = {
  phone: '(946)461-30-12',
  message: 'within',
  sendAt: dayjs('2026-01-28T15:10'),
  status: 'PENDING',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
