import { IClinic } from 'app/entities/clinic/clinic.model';

export interface IPatient {
  id: number;
  fullName?: string | null;
  phone?: string | null;
  clinic?: Pick<IClinic, 'id'> | null;
}

export type NewPatient = Omit<IPatient, 'id'> & { id: null };
