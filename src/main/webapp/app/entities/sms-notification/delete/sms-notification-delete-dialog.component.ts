import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISmsNotification } from '../sms-notification.model';
import { SmsNotificationService } from '../service/sms-notification.service';

@Component({
  templateUrl: './sms-notification-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SmsNotificationDeleteDialogComponent {
  smsNotification?: ISmsNotification;

  protected smsNotificationService = inject(SmsNotificationService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.smsNotificationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
