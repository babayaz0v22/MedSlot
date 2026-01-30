import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IClinic } from '../clinic.model';
import { ClinicService } from '../service/clinic.service';

@Component({
  templateUrl: './clinic-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ClinicDeleteDialogComponent {
  clinic?: IClinic;

  protected clinicService = inject(ClinicService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.clinicService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
