import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAppointment, NewAppointment } from '../appointment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAppointment for edit and NewAppointmentFormGroupInput for create.
 */
type AppointmentFormGroupInput = IAppointment | PartialWithRequiredKeyOf<NewAppointment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAppointment | NewAppointment> = Omit<T, 'appointmentDateTime'> & {
  appointmentDateTime?: string | null;
};

type AppointmentFormRawValue = FormValueOf<IAppointment>;

type NewAppointmentFormRawValue = FormValueOf<NewAppointment>;

type AppointmentFormDefaults = Pick<NewAppointment, 'id' | 'appointmentDateTime'>;

type AppointmentFormGroupContent = {
  id: FormControl<AppointmentFormRawValue['id'] | NewAppointment['id']>;
  appointmentDateTime: FormControl<AppointmentFormRawValue['appointmentDateTime']>;
  status: FormControl<AppointmentFormRawValue['status']>;
  note: FormControl<AppointmentFormRawValue['note']>;
  clinic: FormControl<AppointmentFormRawValue['clinic']>;
  doctor: FormControl<AppointmentFormRawValue['doctor']>;
  patient: FormControl<AppointmentFormRawValue['patient']>;
};

export type AppointmentFormGroup = FormGroup<AppointmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AppointmentFormService {
  createAppointmentFormGroup(appointment: AppointmentFormGroupInput = { id: null }): AppointmentFormGroup {
    const appointmentRawValue = this.convertAppointmentToAppointmentRawValue({
      ...this.getFormDefaults(),
      ...appointment,
    });
    return new FormGroup<AppointmentFormGroupContent>({
      id: new FormControl(
        { value: appointmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      appointmentDateTime: new FormControl(appointmentRawValue.appointmentDateTime, {
        validators: [Validators.required],
      }),
      status: new FormControl(appointmentRawValue.status, {
        validators: [Validators.required],
      }),
      note: new FormControl(appointmentRawValue.note),
      clinic: new FormControl(appointmentRawValue.clinic),
      doctor: new FormControl(appointmentRawValue.doctor),
      patient: new FormControl(appointmentRawValue.patient),
    });
  }

  getAppointment(form: AppointmentFormGroup): IAppointment | NewAppointment {
    return this.convertAppointmentRawValueToAppointment(form.getRawValue() as AppointmentFormRawValue | NewAppointmentFormRawValue);
  }

  resetForm(form: AppointmentFormGroup, appointment: AppointmentFormGroupInput): void {
    const appointmentRawValue = this.convertAppointmentToAppointmentRawValue({ ...this.getFormDefaults(), ...appointment });
    form.reset(
      {
        ...appointmentRawValue,
        id: { value: appointmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AppointmentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      appointmentDateTime: currentTime,
    };
  }

  private convertAppointmentRawValueToAppointment(
    rawAppointment: AppointmentFormRawValue | NewAppointmentFormRawValue,
  ): IAppointment | NewAppointment {
    return {
      ...rawAppointment,
      appointmentDateTime: dayjs(rawAppointment.appointmentDateTime, DATE_TIME_FORMAT),
    };
  }

  private convertAppointmentToAppointmentRawValue(
    appointment: IAppointment | (Partial<NewAppointment> & AppointmentFormDefaults),
  ): AppointmentFormRawValue | PartialWithRequiredKeyOf<NewAppointmentFormRawValue> {
    return {
      ...appointment,
      appointmentDateTime: appointment.appointmentDateTime ? appointment.appointmentDateTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
