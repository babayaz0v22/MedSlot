import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDoctorSchedule } from '../doctor-schedule.model';
import { DoctorScheduleService } from '../service/doctor-schedule.service';

const doctorScheduleResolve = (route: ActivatedRouteSnapshot): Observable<null | IDoctorSchedule> => {
  const id = route.params.id;
  if (id) {
    return inject(DoctorScheduleService)
      .find(id)
      .pipe(
        mergeMap((doctorSchedule: HttpResponse<IDoctorSchedule>) => {
          if (doctorSchedule.body) {
            return of(doctorSchedule.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default doctorScheduleResolve;
