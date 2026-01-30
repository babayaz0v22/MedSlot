import dayjs from 'dayjs/esm';
import { IDoctor } from 'app/entities/doctor/doctor.model';

export interface IDoctorSchedule {
  id: number;
  dayOfWeek?: number | null;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  doctor?: Pick<IDoctor, 'id'> | null;
}

export type NewDoctorSchedule = Omit<IDoctorSchedule, 'id'> & { id: null };
