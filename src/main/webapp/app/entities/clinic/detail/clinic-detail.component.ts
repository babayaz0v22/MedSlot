import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IClinic } from '../clinic.model';

@Component({
  selector: 'jhi-clinic-detail',
  templateUrl: './clinic-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ClinicDetailComponent {
  clinic = input<IClinic | null>(null);

  previousState(): void {
    window.history.back();
  }
}
