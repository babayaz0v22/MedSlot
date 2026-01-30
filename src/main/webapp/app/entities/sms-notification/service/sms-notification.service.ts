import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISmsNotification, NewSmsNotification } from '../sms-notification.model';

export type PartialUpdateSmsNotification = Partial<ISmsNotification> & Pick<ISmsNotification, 'id'>;

type RestOf<T extends ISmsNotification | NewSmsNotification> = Omit<T, 'sendAt' | 'sentAt'> & {
  sendAt?: string | null;
  sentAt?: string | null;
};

export type RestSmsNotification = RestOf<ISmsNotification>;

export type NewRestSmsNotification = RestOf<NewSmsNotification>;

export type PartialUpdateRestSmsNotification = RestOf<PartialUpdateSmsNotification>;

export type EntityResponseType = HttpResponse<ISmsNotification>;
export type EntityArrayResponseType = HttpResponse<ISmsNotification[]>;

@Injectable({ providedIn: 'root' })
export class SmsNotificationService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sms-notifications');

  create(smsNotification: NewSmsNotification): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(smsNotification);
    return this.http
      .post<RestSmsNotification>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(smsNotification: ISmsNotification): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(smsNotification);
    return this.http
      .put<RestSmsNotification>(`${this.resourceUrl}/${this.getSmsNotificationIdentifier(smsNotification)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(smsNotification: PartialUpdateSmsNotification): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(smsNotification);
    return this.http
      .patch<RestSmsNotification>(`${this.resourceUrl}/${this.getSmsNotificationIdentifier(smsNotification)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSmsNotification>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSmsNotification[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSmsNotificationIdentifier(smsNotification: Pick<ISmsNotification, 'id'>): number {
    return smsNotification.id;
  }

  compareSmsNotification(o1: Pick<ISmsNotification, 'id'> | null, o2: Pick<ISmsNotification, 'id'> | null): boolean {
    return o1 && o2 ? this.getSmsNotificationIdentifier(o1) === this.getSmsNotificationIdentifier(o2) : o1 === o2;
  }

  addSmsNotificationToCollectionIfMissing<Type extends Pick<ISmsNotification, 'id'>>(
    smsNotificationCollection: Type[],
    ...smsNotificationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const smsNotifications: Type[] = smsNotificationsToCheck.filter(isPresent);
    if (smsNotifications.length > 0) {
      const smsNotificationCollectionIdentifiers = smsNotificationCollection.map(smsNotificationItem =>
        this.getSmsNotificationIdentifier(smsNotificationItem),
      );
      const smsNotificationsToAdd = smsNotifications.filter(smsNotificationItem => {
        const smsNotificationIdentifier = this.getSmsNotificationIdentifier(smsNotificationItem);
        if (smsNotificationCollectionIdentifiers.includes(smsNotificationIdentifier)) {
          return false;
        }
        smsNotificationCollectionIdentifiers.push(smsNotificationIdentifier);
        return true;
      });
      return [...smsNotificationsToAdd, ...smsNotificationCollection];
    }
    return smsNotificationCollection;
  }

  protected convertDateFromClient<T extends ISmsNotification | NewSmsNotification | PartialUpdateSmsNotification>(
    smsNotification: T,
  ): RestOf<T> {
    return {
      ...smsNotification,
      sendAt: smsNotification.sendAt?.toJSON() ?? null,
      sentAt: smsNotification.sentAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSmsNotification: RestSmsNotification): ISmsNotification {
    return {
      ...restSmsNotification,
      sendAt: restSmsNotification.sendAt ? dayjs(restSmsNotification.sendAt) : undefined,
      sentAt: restSmsNotification.sentAt ? dayjs(restSmsNotification.sentAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSmsNotification>): HttpResponse<ISmsNotification> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSmsNotification[]>): HttpResponse<ISmsNotification[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
