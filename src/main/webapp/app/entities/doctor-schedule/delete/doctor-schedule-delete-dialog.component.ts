import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDoctorSchedule } from '../doctor-schedule.model';
import { DoctorScheduleService } from '../service/doctor-schedule.service';

@Component({
  templateUrl: './doctor-schedule-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DoctorScheduleDeleteDialogComponent {
  doctorSchedule?: IDoctorSchedule;

  protected doctorScheduleService = inject(DoctorScheduleService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.doctorScheduleService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
