import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IClinic } from 'app/entities/clinic/clinic.model';
import { ClinicService } from 'app/entities/clinic/service/clinic.service';
import { IDoctor } from '../doctor.model';
import { DoctorService } from '../service/doctor.service';
import { DoctorFormGroup, DoctorFormService } from './doctor-form.service';

@Component({
  selector: 'jhi-doctor-update',
  templateUrl: './doctor-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DoctorUpdateComponent implements OnInit {
  isSaving = false;
  doctor: IDoctor | null = null;

  clinicsSharedCollection: IClinic[] = [];

  protected doctorService = inject(DoctorService);
  protected doctorFormService = inject(DoctorFormService);
  protected clinicService = inject(ClinicService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DoctorFormGroup = this.doctorFormService.createDoctorFormGroup();

  compareClinic = (o1: IClinic | null, o2: IClinic | null): boolean => this.clinicService.compareClinic(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ doctor }) => {
      this.doctor = doctor;
      if (doctor) {
        this.updateForm(doctor);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const doctor = this.doctorFormService.getDoctor(this.editForm);
    if (doctor.id !== null) {
      this.subscribeToSaveResponse(this.doctorService.update(doctor));
    } else {
      this.subscribeToSaveResponse(this.doctorService.create(doctor));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDoctor>>): void {
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

  protected updateForm(doctor: IDoctor): void {
    this.doctor = doctor;
    this.doctorFormService.resetForm(this.editForm, doctor);

    this.clinicsSharedCollection = this.clinicService.addClinicToCollectionIfMissing<IClinic>(this.clinicsSharedCollection, doctor.clinic);
  }

  protected loadRelationshipsOptions(): void {
    this.clinicService
      .query()
      .pipe(map((res: HttpResponse<IClinic[]>) => res.body ?? []))
      .pipe(map((clinics: IClinic[]) => this.clinicService.addClinicToCollectionIfMissing<IClinic>(clinics, this.doctor?.clinic)))
      .subscribe((clinics: IClinic[]) => (this.clinicsSharedCollection = clinics));
  }
}
