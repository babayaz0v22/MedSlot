import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';
import { IDoctorSchedule } from '../doctor-schedule.model';
import { DoctorScheduleService } from '../service/doctor-schedule.service';
import { DoctorScheduleFormGroup, DoctorScheduleFormService } from './doctor-schedule-form.service';

@Component({
  selector: 'jhi-doctor-schedule-update',
  templateUrl: './doctor-schedule-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DoctorScheduleUpdateComponent implements OnInit {
  isSaving = false;
  doctorSchedule: IDoctorSchedule | null = null;

  doctorsSharedCollection: IDoctor[] = [];

  protected doctorScheduleService = inject(DoctorScheduleService);
  protected doctorScheduleFormService = inject(DoctorScheduleFormService);
  protected doctorService = inject(DoctorService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DoctorScheduleFormGroup = this.doctorScheduleFormService.createDoctorScheduleFormGroup();

  compareDoctor = (o1: IDoctor | null, o2: IDoctor | null): boolean => this.doctorService.compareDoctor(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ doctorSchedule }) => {
      this.doctorSchedule = doctorSchedule;
      if (doctorSchedule) {
        this.updateForm(doctorSchedule);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const doctorSchedule = this.doctorScheduleFormService.getDoctorSchedule(this.editForm);
    if (doctorSchedule.id !== null) {
      this.subscribeToSaveResponse(this.doctorScheduleService.update(doctorSchedule));
    } else {
      this.subscribeToSaveResponse(this.doctorScheduleService.create(doctorSchedule));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDoctorSchedule>>): void {
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

  protected updateForm(doctorSchedule: IDoctorSchedule): void {
    this.doctorSchedule = doctorSchedule;
    this.doctorScheduleFormService.resetForm(this.editForm, doctorSchedule);

    this.doctorsSharedCollection = this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(
      this.doctorsSharedCollection,
      doctorSchedule.doctor,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.doctorService
      .query()
      .pipe(map((res: HttpResponse<IDoctor[]>) => res.body ?? []))
      .pipe(map((doctors: IDoctor[]) => this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(doctors, this.doctorSchedule?.doctor)))
      .subscribe((doctors: IDoctor[]) => (this.doctorsSharedCollection = doctors));
  }
}
