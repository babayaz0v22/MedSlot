import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISmsNotification, NewSmsNotification } from '../sms-notification.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISmsNotification for edit and NewSmsNotificationFormGroupInput for create.
 */
type SmsNotificationFormGroupInput = ISmsNotification | PartialWithRequiredKeyOf<NewSmsNotification>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISmsNotification | NewSmsNotification> = Omit<T, 'sendAt' | 'sentAt'> & {
  sendAt?: string | null;
  sentAt?: string | null;
};

type SmsNotificationFormRawValue = FormValueOf<ISmsNotification>;

type NewSmsNotificationFormRawValue = FormValueOf<NewSmsNotification>;

type SmsNotificationFormDefaults = Pick<NewSmsNotification, 'id' | 'sendAt' | 'sentAt'>;

type SmsNotificationFormGroupContent = {
  id: FormControl<SmsNotificationFormRawValue['id'] | NewSmsNotification['id']>;
  phone: FormControl<SmsNotificationFormRawValue['phone']>;
  message: FormControl<SmsNotificationFormRawValue['message']>;
  sendAt: FormControl<SmsNotificationFormRawValue['sendAt']>;
  sentAt: FormControl<SmsNotificationFormRawValue['sentAt']>;
  status: FormControl<SmsNotificationFormRawValue['status']>;
  errorMessage: FormControl<SmsNotificationFormRawValue['errorMessage']>;
  clinic: FormControl<SmsNotificationFormRawValue['clinic']>;
  appointment: FormControl<SmsNotificationFormRawValue['appointment']>;
};

export type SmsNotificationFormGroup = FormGroup<SmsNotificationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SmsNotificationFormService {
  createSmsNotificationFormGroup(smsNotification: SmsNotificationFormGroupInput = { id: null }): SmsNotificationFormGroup {
    const smsNotificationRawValue = this.convertSmsNotificationToSmsNotificationRawValue({
      ...this.getFormDefaults(),
      ...smsNotification,
    });
    return new FormGroup<SmsNotificationFormGroupContent>({
      id: new FormControl(
        { value: smsNotificationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      phone: new FormControl(smsNotificationRawValue.phone, {
        validators: [Validators.required],
      }),
      message: new FormControl(smsNotificationRawValue.message, {
        validators: [Validators.required],
      }),
      sendAt: new FormControl(smsNotificationRawValue.sendAt, {
        validators: [Validators.required],
      }),
      sentAt: new FormControl(smsNotificationRawValue.sentAt),
      status: new FormControl(smsNotificationRawValue.status, {
        validators: [Validators.required],
      }),
      errorMessage: new FormControl(smsNotificationRawValue.errorMessage),
      clinic: new FormControl(smsNotificationRawValue.clinic),
      appointment: new FormControl(smsNotificationRawValue.appointment),
    });
  }

  getSmsNotification(form: SmsNotificationFormGroup): ISmsNotification | NewSmsNotification {
    return this.convertSmsNotificationRawValueToSmsNotification(
      form.getRawValue() as SmsNotificationFormRawValue | NewSmsNotificationFormRawValue,
    );
  }

  resetForm(form: SmsNotificationFormGroup, smsNotification: SmsNotificationFormGroupInput): void {
    const smsNotificationRawValue = this.convertSmsNotificationToSmsNotificationRawValue({ ...this.getFormDefaults(), ...smsNotification });
    form.reset(
      {
        ...smsNotificationRawValue,
        id: { value: smsNotificationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SmsNotificationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      sendAt: currentTime,
      sentAt: currentTime,
    };
  }

  private convertSmsNotificationRawValueToSmsNotification(
    rawSmsNotification: SmsNotificationFormRawValue | NewSmsNotificationFormRawValue,
  ): ISmsNotification | NewSmsNotification {
    return {
      ...rawSmsNotification,
      sendAt: dayjs(rawSmsNotification.sendAt, DATE_TIME_FORMAT),
      sentAt: dayjs(rawSmsNotification.sentAt, DATE_TIME_FORMAT),
    };
  }

  private convertSmsNotificationToSmsNotificationRawValue(
    smsNotification: ISmsNotification | (Partial<NewSmsNotification> & SmsNotificationFormDefaults),
  ): SmsNotificationFormRawValue | PartialWithRequiredKeyOf<NewSmsNotificationFormRawValue> {
    return {
      ...smsNotification,
      sendAt: smsNotification.sendAt ? smsNotification.sendAt.format(DATE_TIME_FORMAT) : undefined,
      sentAt: smsNotification.sentAt ? smsNotification.sentAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
