import { IClinic } from 'app/entities/clinic/clinic.model';

export interface IDoctor {
  id: number;
  specialty?: string | null;
  phone?: string | null;
  clinic?: Pick<IClinic, 'id'> | null;
}

export type NewDoctor = Omit<IDoctor, 'id'> & { id: null };
