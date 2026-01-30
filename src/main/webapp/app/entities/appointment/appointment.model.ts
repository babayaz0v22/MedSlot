import dayjs from 'dayjs/esm';
import { IClinic } from 'app/entities/clinic/clinic.model';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { IPatient } from 'app/entities/patient/patient.model';
import { AppointmentStatus } from 'app/entities/enumerations/appointment-status.model';

export interface IAppointment {
  id: number;
  appointmentDateTime?: dayjs.Dayjs | null;
  status?: keyof typeof AppointmentStatus | null;
  note?: string | null;
  clinic?: Pick<IClinic, 'id'> | null;
  doctor?: Pick<IDoctor, 'id'> | null;
  patient?: Pick<IPatient, 'id'> | null;
}

export type NewAppointment = Omit<IAppointment, 'id'> & { id: null };
