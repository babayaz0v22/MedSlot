import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDoctorSchedule, NewDoctorSchedule } from '../doctor-schedule.model';

export type PartialUpdateDoctorSchedule = Partial<IDoctorSchedule> & Pick<IDoctorSchedule, 'id'>;

type RestOf<T extends IDoctorSchedule | NewDoctorSchedule> = Omit<T, 'startTime' | 'endTime'> & {
  startTime?: string | null;
  endTime?: string | null;
};

export type RestDoctorSchedule = RestOf<IDoctorSchedule>;

export type NewRestDoctorSchedule = RestOf<NewDoctorSchedule>;

export type PartialUpdateRestDoctorSchedule = RestOf<PartialUpdateDoctorSchedule>;

export type EntityResponseType = HttpResponse<IDoctorSchedule>;
export type EntityArrayResponseType = HttpResponse<IDoctorSchedule[]>;

@Injectable({ providedIn: 'root' })
export class DoctorScheduleService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/doctor-schedules');

  create(doctorSchedule: NewDoctorSchedule): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(doctorSchedule);
    return this.http
      .post<RestDoctorSchedule>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(doctorSchedule: IDoctorSchedule): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(doctorSchedule);
    return this.http
      .put<RestDoctorSchedule>(`${this.resourceUrl}/${this.getDoctorScheduleIdentifier(doctorSchedule)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(doctorSchedule: PartialUpdateDoctorSchedule): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(doctorSchedule);
    return this.http
      .patch<RestDoctorSchedule>(`${this.resourceUrl}/${this.getDoctorScheduleIdentifier(doctorSchedule)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDoctorSchedule>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDoctorSchedule[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDoctorScheduleIdentifier(doctorSchedule: Pick<IDoctorSchedule, 'id'>): number {
    return doctorSchedule.id;
  }

  compareDoctorSchedule(o1: Pick<IDoctorSchedule, 'id'> | null, o2: Pick<IDoctorSchedule, 'id'> | null): boolean {
    return o1 && o2 ? this.getDoctorScheduleIdentifier(o1) === this.getDoctorScheduleIdentifier(o2) : o1 === o2;
  }

  addDoctorScheduleToCollectionIfMissing<Type extends Pick<IDoctorSchedule, 'id'>>(
    doctorScheduleCollection: Type[],
    ...doctorSchedulesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const doctorSchedules: Type[] = doctorSchedulesToCheck.filter(isPresent);
    if (doctorSchedules.length > 0) {
      const doctorScheduleCollectionIdentifiers = doctorScheduleCollection.map(doctorScheduleItem =>
        this.getDoctorScheduleIdentifier(doctorScheduleItem),
      );
      const doctorSchedulesToAdd = doctorSchedules.filter(doctorScheduleItem => {
        const doctorScheduleIdentifier = this.getDoctorScheduleIdentifier(doctorScheduleItem);
        if (doctorScheduleCollectionIdentifiers.includes(doctorScheduleIdentifier)) {
          return false;
        }
        doctorScheduleCollectionIdentifiers.push(doctorScheduleIdentifier);
        return true;
      });
      return [...doctorSchedulesToAdd, ...doctorScheduleCollection];
    }
    return doctorScheduleCollection;
  }

  protected convertDateFromClient<T extends IDoctorSchedule | NewDoctorSchedule | PartialUpdateDoctorSchedule>(
    doctorSchedule: T,
  ): RestOf<T> {
    return {
      ...doctorSchedule,
      startTime: doctorSchedule.startTime?.toJSON() ?? null,
      endTime: doctorSchedule.endTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restDoctorSchedule: RestDoctorSchedule): IDoctorSchedule {
    return {
      ...restDoctorSchedule,
      startTime: restDoctorSchedule.startTime ? dayjs(restDoctorSchedule.startTime) : undefined,
      endTime: restDoctorSchedule.endTime ? dayjs(restDoctorSchedule.endTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDoctorSchedule>): HttpResponse<IDoctorSchedule> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDoctorSchedule[]>): HttpResponse<IDoctorSchedule[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
