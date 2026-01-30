import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDoctorSchedule, NewDoctorSchedule } from '../doctor-schedule.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDoctorSchedule for edit and NewDoctorScheduleFormGroupInput for create.
 */
type DoctorScheduleFormGroupInput = IDoctorSchedule | PartialWithRequiredKeyOf<NewDoctorSchedule>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDoctorSchedule | NewDoctorSchedule> = Omit<T, 'startTime' | 'endTime'> & {
  startTime?: string | null;
  endTime?: string | null;
};

type DoctorScheduleFormRawValue = FormValueOf<IDoctorSchedule>;

type NewDoctorScheduleFormRawValue = FormValueOf<NewDoctorSchedule>;

type DoctorScheduleFormDefaults = Pick<NewDoctorSchedule, 'id' | 'startTime' | 'endTime'>;

type DoctorScheduleFormGroupContent = {
  id: FormControl<DoctorScheduleFormRawValue['id'] | NewDoctorSchedule['id']>;
  dayOfWeek: FormControl<DoctorScheduleFormRawValue['dayOfWeek']>;
  startTime: FormControl<DoctorScheduleFormRawValue['startTime']>;
  endTime: FormControl<DoctorScheduleFormRawValue['endTime']>;
  doctor: FormControl<DoctorScheduleFormRawValue['doctor']>;
};

export type DoctorScheduleFormGroup = FormGroup<DoctorScheduleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DoctorScheduleFormService {
  createDoctorScheduleFormGroup(doctorSchedule: DoctorScheduleFormGroupInput = { id: null }): DoctorScheduleFormGroup {
    const doctorScheduleRawValue = this.convertDoctorScheduleToDoctorScheduleRawValue({
      ...this.getFormDefaults(),
      ...doctorSchedule,
    });
    return new FormGroup<DoctorScheduleFormGroupContent>({
      id: new FormControl(
        { value: doctorScheduleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      dayOfWeek: new FormControl(doctorScheduleRawValue.dayOfWeek, {
        validators: [Validators.required, Validators.min(1), Validators.max(7)],
      }),
      startTime: new FormControl(doctorScheduleRawValue.startTime, {
        validators: [Validators.required],
      }),
      endTime: new FormControl(doctorScheduleRawValue.endTime, {
        validators: [Validators.required],
      }),
      doctor: new FormControl(doctorScheduleRawValue.doctor),
    });
  }

  getDoctorSchedule(form: DoctorScheduleFormGroup): IDoctorSchedule | NewDoctorSchedule {
    return this.convertDoctorScheduleRawValueToDoctorSchedule(
      form.getRawValue() as DoctorScheduleFormRawValue | NewDoctorScheduleFormRawValue,
    );
  }

  resetForm(form: DoctorScheduleFormGroup, doctorSchedule: DoctorScheduleFormGroupInput): void {
    const doctorScheduleRawValue = this.convertDoctorScheduleToDoctorScheduleRawValue({ ...this.getFormDefaults(), ...doctorSchedule });
    form.reset(
      {
        ...doctorScheduleRawValue,
        id: { value: doctorScheduleRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DoctorScheduleFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startTime: currentTime,
      endTime: currentTime,
    };
  }

  private convertDoctorScheduleRawValueToDoctorSchedule(
    rawDoctorSchedule: DoctorScheduleFormRawValue | NewDoctorScheduleFormRawValue,
  ): IDoctorSchedule | NewDoctorSchedule {
    return {
      ...rawDoctorSchedule,
      startTime: dayjs(rawDoctorSchedule.startTime, DATE_TIME_FORMAT),
      endTime: dayjs(rawDoctorSchedule.endTime, DATE_TIME_FORMAT),
    };
  }

  private convertDoctorScheduleToDoctorScheduleRawValue(
    doctorSchedule: IDoctorSchedule | (Partial<NewDoctorSchedule> & DoctorScheduleFormDefaults),
  ): DoctorScheduleFormRawValue | PartialWithRequiredKeyOf<NewDoctorScheduleFormRawValue> {
    return {
      ...doctorSchedule,
      startTime: doctorSchedule.startTime ? doctorSchedule.startTime.format(DATE_TIME_FORMAT) : undefined,
      endTime: doctorSchedule.endTime ? doctorSchedule.endTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
