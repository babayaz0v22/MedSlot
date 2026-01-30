import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IClinic, NewClinic } from '../clinic.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClinic for edit and NewClinicFormGroupInput for create.
 */
type ClinicFormGroupInput = IClinic | PartialWithRequiredKeyOf<NewClinic>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IClinic | NewClinic> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type ClinicFormRawValue = FormValueOf<IClinic>;

type NewClinicFormRawValue = FormValueOf<NewClinic>;

type ClinicFormDefaults = Pick<NewClinic, 'id' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type ClinicFormGroupContent = {
  id: FormControl<ClinicFormRawValue['id'] | NewClinic['id']>;
  name: FormControl<ClinicFormRawValue['name']>;
  phone: FormControl<ClinicFormRawValue['phone']>;
  address: FormControl<ClinicFormRawValue['address']>;
  active: FormControl<ClinicFormRawValue['active']>;
  createdBy: FormControl<ClinicFormRawValue['createdBy']>;
  createdDate: FormControl<ClinicFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<ClinicFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<ClinicFormRawValue['lastModifiedDate']>;
};

export type ClinicFormGroup = FormGroup<ClinicFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClinicFormService {
  createClinicFormGroup(clinic: ClinicFormGroupInput = { id: null }): ClinicFormGroup {
    const clinicRawValue = this.convertClinicToClinicRawValue({
      ...this.getFormDefaults(),
      ...clinic,
    });
    return new FormGroup<ClinicFormGroupContent>({
      id: new FormControl(
        { value: clinicRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(clinicRawValue.name, {
        validators: [Validators.required],
      }),
      phone: new FormControl(clinicRawValue.phone),
      address: new FormControl(clinicRawValue.address),
      active: new FormControl(clinicRawValue.active, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(clinicRawValue.createdBy, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      createdDate: new FormControl(clinicRawValue.createdDate),
      lastModifiedBy: new FormControl(clinicRawValue.lastModifiedBy, {
        validators: [Validators.maxLength(50)],
      }),
      lastModifiedDate: new FormControl(clinicRawValue.lastModifiedDate),
    });
  }

  getClinic(form: ClinicFormGroup): IClinic | NewClinic {
    return this.convertClinicRawValueToClinic(form.getRawValue() as ClinicFormRawValue | NewClinicFormRawValue);
  }

  resetForm(form: ClinicFormGroup, clinic: ClinicFormGroupInput): void {
    const clinicRawValue = this.convertClinicToClinicRawValue({ ...this.getFormDefaults(), ...clinic });
    form.reset(
      {
        ...clinicRawValue,
        id: { value: clinicRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ClinicFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertClinicRawValueToClinic(rawClinic: ClinicFormRawValue | NewClinicFormRawValue): IClinic | NewClinic {
    return {
      ...rawClinic,
      createdDate: dayjs(rawClinic.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawClinic.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertClinicToClinicRawValue(
    clinic: IClinic | (Partial<NewClinic> & ClinicFormDefaults),
  ): ClinicFormRawValue | PartialWithRequiredKeyOf<NewClinicFormRawValue> {
    return {
      ...clinic,
      createdDate: clinic.createdDate ? clinic.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: clinic.lastModifiedDate ? clinic.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
