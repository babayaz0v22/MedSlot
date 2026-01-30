import { IDoctor, NewDoctor } from './doctor.model';

export const sampleWithRequiredData: IDoctor = {
  id: 670,
  specialty: 'toothbrush righteously boo',
};

export const sampleWithPartialData: IDoctor = {
  id: 32131,
  specialty: 'with worriedly rejoin',
  phone: '(938)509-81-13',
};

export const sampleWithFullData: IDoctor = {
  id: 20131,
  specialty: 'wildly ick',
  phone: '(938)368-30-36',
};

export const sampleWithNewData: NewDoctor = {
  specialty: 'yum',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
