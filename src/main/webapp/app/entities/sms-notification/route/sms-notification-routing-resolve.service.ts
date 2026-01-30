import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISmsNotification } from '../sms-notification.model';
import { SmsNotificationService } from '../service/sms-notification.service';

const smsNotificationResolve = (route: ActivatedRouteSnapshot): Observable<null | ISmsNotification> => {
  const id = route.params.id;
  if (id) {
    return inject(SmsNotificationService)
      .find(id)
      .pipe(
        mergeMap((smsNotification: HttpResponse<ISmsNotification>) => {
          if (smsNotification.body) {
            return of(smsNotification.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default smsNotificationResolve;
