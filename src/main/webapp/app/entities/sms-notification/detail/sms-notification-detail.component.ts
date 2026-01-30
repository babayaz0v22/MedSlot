import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ISmsNotification } from '../sms-notification.model';

@Component({
  selector: 'jhi-sms-notification-detail',
  templateUrl: './sms-notification-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class SmsNotificationDetailComponent {
  smsNotification = input<ISmsNotification | null>(null);

  previousState(): void {
    window.history.back();
  }
}
