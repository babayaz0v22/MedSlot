import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IClinic } from 'app/entities/clinic/clinic.model';
import { ClinicService } from 'app/entities/clinic/service/clinic.service';
import { IPatient } from '../patient.model';
import { PatientService } from '../service/patient.service';
import { PatientFormGroup, PatientFormService } from './patient-form.service';

@Component({
  selector: 'jhi-patient-update',
  templateUrl: './patient-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PatientUpdateComponent implements OnInit {
  isSaving = false;
  patient: IPatient | null = null;

  clinicsSharedCollection: IClinic[] = [];

  protected patientService = inject(PatientService);
  protected patientFormService = inject(PatientFormService);
  protected clinicService = inject(ClinicService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PatientFormGroup = this.patientFormService.createPatientFormGroup();

  compareClinic = (o1: IClinic | null, o2: IClinic | null): boolean => this.clinicService.compareClinic(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ patient }) => {
      this.patient = patient;
      if (patient) {
        this.updateForm(patient);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const patient = this.patientFormService.getPatient(this.editForm);
    if (patient.id !== null) {
      this.subscribeToSaveResponse(this.patientService.update(patient));
    } else {
      this.subscribeToSaveResponse(this.patientService.create(patient));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPatient>>): void {
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

  protected updateForm(patient: IPatient): void {
    this.patient = patient;
    this.patientFormService.resetForm(this.editForm, patient);

    this.clinicsSharedCollection = this.clinicService.addClinicToCollectionIfMissing<IClinic>(this.clinicsSharedCollection, patient.clinic);
  }

  protected loadRelationshipsOptions(): void {
    this.clinicService
      .query()
      .pipe(map((res: HttpResponse<IClinic[]>) => res.body ?? []))
      .pipe(map((clinics: IClinic[]) => this.clinicService.addClinicToCollectionIfMissing<IClinic>(clinics, this.patient?.clinic)))
      .subscribe((clinics: IClinic[]) => (this.clinicsSharedCollection = clinics));
  }
}
