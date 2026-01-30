import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IDoctorSchedule } from '../doctor-schedule.model';

@Component({
  selector: 'jhi-doctor-schedule-detail',
  templateUrl: './doctor-schedule-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class DoctorScheduleDetailComponent {
  doctorSchedule = input<IDoctorSchedule | null>(null);

  previousState(): void {
    window.history.back();
  }
}
