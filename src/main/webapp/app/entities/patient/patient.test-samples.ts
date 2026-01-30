import { IPatient, NewPatient } from './patient.model';

export const sampleWithRequiredData: IPatient = {
  id: 2514,
  fullName: 'custody if who',
  phone: '(928)806-84-73',
};

export const sampleWithPartialData: IPatient = {
  id: 12268,
  fullName: 'parody',
  phone: '(934)747-54-54',
};

export const sampleWithFullData: IPatient = {
  id: 9819,
  fullName: 'an',
  phone: '(936)504-99-53',
};

export const sampleWithNewData: NewPatient = {
  fullName: 'kick labourer brr',
  phone: '(966)664-64-98',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
