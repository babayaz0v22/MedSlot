import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IClinic } from '../clinic.model';
import { ClinicService } from '../service/clinic.service';

const clinicResolve = (route: ActivatedRouteSnapshot): Observable<null | IClinic> => {
  const id = route.params.id;
  if (id) {
    return inject(ClinicService)
      .find(id)
      .pipe(
        mergeMap((clinic: HttpResponse<IClinic>) => {
          if (clinic.body) {
            return of(clinic.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default clinicResolve;
