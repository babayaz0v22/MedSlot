import dayjs from 'dayjs/esm';
import { IClinic } from 'app/entities/clinic/clinic.model';
import { IAppointment } from 'app/entities/appointment/appointment.model';
import { SmsStatus } from 'app/entities/enumerations/sms-status.model';

export interface ISmsNotification {
  id: number;
  phone?: string | null;
  message?: string | null;
  sendAt?: dayjs.Dayjs | null;
  sentAt?: dayjs.Dayjs | null;
  status?: keyof typeof SmsStatus | null;
  errorMessage?: string | null;
  clinic?: Pick<IClinic, 'id'> | null;
  appointment?: Pick<IAppointment, 'id'> | null;
}

export type NewSmsNotification = Omit<ISmsNotification, 'id'> & { id: null };
