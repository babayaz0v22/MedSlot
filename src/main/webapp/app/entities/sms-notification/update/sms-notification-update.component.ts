import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IClinic } from 'app/entities/clinic/clinic.model';
import { ClinicService } from 'app/entities/clinic/service/clinic.service';
import { IAppointment } from 'app/entities/appointment/appointment.model';
import { AppointmentService } from 'app/entities/appointment/service/appointment.service';
import { SmsStatus } from 'app/entities/enumerations/sms-status.model';
import { SmsNotificationService } from '../service/sms-notification.service';
import { ISmsNotification } from '../sms-notification.model';
import { SmsNotificationFormGroup, SmsNotificationFormService } from './sms-notification-form.service';

@Component({
  selector: 'jhi-sms-notification-update',
  templateUrl: './sms-notification-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SmsNotificationUpdateComponent implements OnInit {
  isSaving = false;
  smsNotification: ISmsNotification | null = null;
  smsStatusValues = Object.keys(SmsStatus);

  clinicsSharedCollection: IClinic[] = [];
  appointmentsSharedCollection: IAppointment[] = [];

  protected smsNotificationService = inject(SmsNotificationService);
  protected smsNotificationFormService = inject(SmsNotificationFormService);
  protected clinicService = inject(ClinicService);
  protected appointmentService = inject(AppointmentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SmsNotificationFormGroup = this.smsNotificationFormService.createSmsNotificationFormGroup();

  compareClinic = (o1: IClinic | null, o2: IClinic | null): boolean => this.clinicService.compareClinic(o1, o2);

  compareAppointment = (o1: IAppointment | null, o2: IAppointment | null): boolean => this.appointmentService.compareAppointment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ smsNotification }) => {
      this.smsNotification = smsNotification;
      if (smsNotification) {
        this.updateForm(smsNotification);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const smsNotification = this.smsNotificationFormService.getSmsNotification(this.editForm);
    if (smsNotification.id !== null) {
      this.subscribeToSaveResponse(this.smsNotificationService.update(smsNotification));
    } else {
      this.subscribeToSaveResponse(this.smsNotificationService.create(smsNotification));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISmsNotification>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(smsNotification: ISmsNotification): void {
    this.smsNotification = smsNotification;
    this.smsNotificationFormService.resetForm(this.editForm, smsNotification);

    this.clinicsSharedCollection = this.clinicService.addClinicToCollectionIfMissing<IClinic>(
      this.clinicsSharedCollection,
      smsNotification.clinic,
    );
    this.appointmentsSharedCollection = this.appointmentService.addAppointmentToCollectionIfMissing<IAppointment>(
      this.appointmentsSharedCollection,
      smsNotification.appointment,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.clinicService
      .query()
      .pipe(map((res: HttpResponse<IClinic[]>) => res.body ?? []))
      .pipe(map((clinics: IClinic[]) => this.clinicService.addClinicToCollectionIfMissing<IClinic>(clinics, this.smsNotification?.clinic)))
      .subscribe((clinics: IClinic[]) => (this.clinicsSharedCollection = clinics));

    this.appointmentService
      .query()
      .pipe(map((res: HttpResponse<IAppointment[]>) => res.body ?? []))
      .pipe(
        map((appointments: IAppointment[]) =>
          this.appointmentService.addAppointmentToCollectionIfMissing<IAppointment>(appointments, this.smsNotification?.appointment),
        ),
      )
      .subscribe((appointments: IAppointment[]) => (this.appointmentsSharedCollection = appointments));
  }
}
