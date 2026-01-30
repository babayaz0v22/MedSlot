import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../doctor-schedule.test-samples';

import { DoctorScheduleFormService } from './doctor-schedule-form.service';

describe('DoctorSchedule Form Service', () => {
  let service: DoctorScheduleFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DoctorScheduleFormService);
  });

  describe('Service methods', () => {
    describe('createDoctorScheduleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDoctorScheduleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dayOfWeek: expect.any(Object),
            startTime: expect.any(Object),
            endTime: expect.any(Object),
            doctor: expect.any(Object),
          }),
        );
      });

      it('passing IDoctorSchedule should create a new form with FormGroup', () => {
        const formGroup = service.createDoctorScheduleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dayOfWeek: expect.any(Object),
            startTime: expect.any(Object),
            endTime: expect.any(Object),
            doctor: expect.any(Object),
          }),
        );
      });
    });

    describe('getDoctorSchedule', () => {
      it('should return NewDoctorSchedule for default DoctorSchedule initial value', () => {
        const formGroup = service.createDoctorScheduleFormGroup(sampleWithNewData);

        const doctorSchedule = service.getDoctorSchedule(formGroup) as any;

        expect(doctorSchedule).toMatchObject(sampleWithNewData);
      });

      it('should return NewDoctorSchedule for empty DoctorSchedule initial value', () => {
        const formGroup = service.createDoctorScheduleFormGroup();

        const doctorSchedule = service.getDoctorSchedule(formGroup) as any;

        expect(doctorSchedule).toMatchObject({});
      });

      it('should return IDoctorSchedule', () => {
        const formGroup = service.createDoctorScheduleFormGroup(sampleWithRequiredData);

        const doctorSchedule = service.getDoctorSchedule(formGroup) as any;

        expect(doctorSchedule).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDoctorSchedule should not enable id FormControl', () => {
        const formGroup = service.createDoctorScheduleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDoctorSchedule should disable id FormControl', () => {
        const formGroup = service.createDoctorScheduleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
