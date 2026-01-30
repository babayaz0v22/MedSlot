import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IClinic } from '../clinic.model';
import { ClinicService } from '../service/clinic.service';
import { ClinicFormGroup, ClinicFormService } from './clinic-form.service';

@Component({
  selector: 'jhi-clinic-update',
  templateUrl: './clinic-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ClinicUpdateComponent implements OnInit {
  isSaving = false;
  clinic: IClinic | null = null;

  protected clinicService = inject(ClinicService);
  protected clinicFormService = inject(ClinicFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ClinicFormGroup = this.clinicFormService.createClinicFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clinic }) => {
      this.clinic = clinic;
      if (clinic) {
        this.updateForm(clinic);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const clinic = this.clinicFormService.getClinic(this.editForm);
    if (clinic.id !== null) {
      this.subscribeToSaveResponse(this.clinicService.update(clinic));
    } else {
      this.subscribeToSaveResponse(this.clinicService.create(clinic));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClinic>>): void {
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

  protected updateForm(clinic: IClinic): void {
    this.clinic = clinic;
    this.clinicFormService.resetForm(this.editForm, clinic);
  }
}
