import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../sms-notification.test-samples';

import { SmsNotificationFormService } from './sms-notification-form.service';

describe('SmsNotification Form Service', () => {
  let service: SmsNotificationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SmsNotificationFormService);
  });

  describe('Service methods', () => {
    describe('createSmsNotificationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSmsNotificationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            phone: expect.any(Object),
            message: expect.any(Object),
            sendAt: expect.any(Object),
            sentAt: expect.any(Object),
            status: expect.any(Object),
            errorMessage: expect.any(Object),
            clinic: expect.any(Object),
            appointment: expect.any(Object),
          }),
        );
      });

      it('passing ISmsNotification should create a new form with FormGroup', () => {
        const formGroup = service.createSmsNotificationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            phone: expect.any(Object),
            message: expect.any(Object),
            sendAt: expect.any(Object),
            sentAt: expect.any(Object),
            status: expect.any(Object),
            errorMessage: expect.any(Object),
            clinic: expect.any(Object),
            appointment: expect.any(Object),
          }),
        );
      });
    });

    describe('getSmsNotification', () => {
      it('should return NewSmsNotification for default SmsNotification initial value', () => {
        const formGroup = service.createSmsNotificationFormGroup(sampleWithNewData);

        const smsNotification = service.getSmsNotification(formGroup) as any;

        expect(smsNotification).toMatchObject(sampleWithNewData);
      });

      it('should return NewSmsNotification for empty SmsNotification initial value', () => {
        const formGroup = service.createSmsNotificationFormGroup();

        const smsNotification = service.getSmsNotification(formGroup) as any;

        expect(smsNotification).toMatchObject({});
      });

      it('should return ISmsNotification', () => {
        const formGroup = service.createSmsNotificationFormGroup(sampleWithRequiredData);

        const smsNotification = service.getSmsNotification(formGroup) as any;

        expect(smsNotification).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISmsNotification should not enable id FormControl', () => {
        const formGroup = service.createSmsNotificationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSmsNotification should disable id FormControl', () => {
        const formGroup = service.createSmsNotificationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
