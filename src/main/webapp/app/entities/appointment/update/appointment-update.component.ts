import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IClinic } from 'app/entities/clinic/clinic.model';
import { ClinicService } from 'app/entities/clinic/service/clinic.service';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';
import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { AppointmentStatus } from 'app/entities/enumerations/appointment-status.model';
import { AppointmentService } from '../service/appointment.service';
import { IAppointment } from '../appointment.model';
import { AppointmentFormGroup, AppointmentFormService } from './appointment-form.service';

@Component({
  selector: 'jhi-appointment-update',
  templateUrl: './appointment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AppointmentUpdateComponent implements OnInit {
  isSaving = false;
  appointment: IAppointment | null = null;
  appointmentStatusValues = Object.keys(AppointmentStatus);

  clinicsSharedCollection: IClinic[] = [];
  doctorsSharedCollection: IDoctor[] = [];
  patientsSharedCollection: IPatient[] = [];

  protected appointmentService = inject(AppointmentService);
  protected appointmentFormService = inject(AppointmentFormService);
  protected clinicService = inject(ClinicService);
  protected doctorService = inject(DoctorService);
  protected patientService = inject(PatientService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AppointmentFormGroup = this.appointmentFormService.createAppointmentFormGroup();

  compareClinic = (o1: IClinic | null, o2: IClinic | null): boolean => this.clinicService.compareClinic(o1, o2);

  compareDoctor = (o1: IDoctor | null, o2: IDoctor | null): boolean => this.doctorService.compareDoctor(o1, o2);

  comparePatient = (o1: IPatient | null, o2: IPatient | null): boolean => this.patientService.comparePatient(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appointment }) => {
      this.appointment = appointment;
      if (appointment) {
        this.updateForm(appointment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appointment = this.appointmentFormService.getAppointment(this.editForm);
    if (appointment.id !== null) {
      this.subscribeToSaveResponse(this.appointmentService.update(appointment));
    } else {
      this.subscribeToSaveResponse(this.appointmentService.create(appointment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppointment>>): void {
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

  protected updateForm(appointment: IAppointment): void {
    this.appointment = appointment;
    this.appointmentFormService.resetForm(this.editForm, appointment);

    this.clinicsSharedCollection = this.clinicService.addClinicToCollectionIfMissing<IClinic>(
      this.clinicsSharedCollection,
      appointment.clinic,
    );
    this.doctorsSharedCollection = this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(
      this.doctorsSharedCollection,
      appointment.doctor,
    );
    this.patientsSharedCollection = this.patientService.addPatientToCollectionIfMissing<IPatient>(
      this.patientsSharedCollection,
      appointment.patient,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.clinicService
      .query()
      .pipe(map((res: HttpResponse<IClinic[]>) => res.body ?? []))
      .pipe(map((clinics: IClinic[]) => this.clinicService.addClinicToCollectionIfMissing<IClinic>(clinics, this.appointment?.clinic)))
      .subscribe((clinics: IClinic[]) => (this.clinicsSharedCollection = clinics));

    this.doctorService
      .query()
      .pipe(map((res: HttpResponse<IDoctor[]>) => res.body ?? []))
      .pipe(map((doctors: IDoctor[]) => this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(doctors, this.appointment?.doctor)))
      .subscribe((doctors: IDoctor[]) => (this.doctorsSharedCollection = doctors));

    this.patientService
      .query()
      .pipe(map((res: HttpResponse<IPatient[]>) => res.body ?? []))
      .pipe(
        map((patients: IPatient[]) => this.patientService.addPatientToCollectionIfMissing<IPatient>(patients, this.appointment?.patient)),
      )
      .subscribe((patients: IPatient[]) => (this.patientsSharedCollection = patients));
  }
}
