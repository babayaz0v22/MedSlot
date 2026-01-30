import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISmsNotification } from '../sms-notification.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../sms-notification.test-samples';

import { RestSmsNotification, SmsNotificationService } from './sms-notification.service';

const requireRestSample: RestSmsNotification = {
  ...sampleWithRequiredData,
  sendAt: sampleWithRequiredData.sendAt?.toJSON(),
  sentAt: sampleWithRequiredData.sentAt?.toJSON(),
};

describe('SmsNotification Service', () => {
  let service: SmsNotificationService;
  let httpMock: HttpTestingController;
  let expectedResult: ISmsNotification | ISmsNotification[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SmsNotificationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a SmsNotification', () => {
      const smsNotification = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(smsNotification).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SmsNotification', () => {
      const smsNotification = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(smsNotification).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SmsNotification', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SmsNotification', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SmsNotification', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSmsNotificationToCollectionIfMissing', () => {
      it('should add a SmsNotification to an empty array', () => {
        const smsNotification: ISmsNotification = sampleWithRequiredData;
        expectedResult = service.addSmsNotificationToCollectionIfMissing([], smsNotification);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(smsNotification);
      });

      it('should not add a SmsNotification to an array that contains it', () => {
        const smsNotification: ISmsNotification = sampleWithRequiredData;
        const smsNotificationCollection: ISmsNotification[] = [
          {
            ...smsNotification,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSmsNotificationToCollectionIfMissing(smsNotificationCollection, smsNotification);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SmsNotification to an array that doesn't contain it", () => {
        const smsNotification: ISmsNotification = sampleWithRequiredData;
        const smsNotificationCollection: ISmsNotification[] = [sampleWithPartialData];
        expectedResult = service.addSmsNotificationToCollectionIfMissing(smsNotificationCollection, smsNotification);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(smsNotification);
      });

      it('should add only unique SmsNotification to an array', () => {
        const smsNotificationArray: ISmsNotification[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const smsNotificationCollection: ISmsNotification[] = [sampleWithRequiredData];
        expectedResult = service.addSmsNotificationToCollectionIfMissing(smsNotificationCollection, ...smsNotificationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const smsNotification: ISmsNotification = sampleWithRequiredData;
        const smsNotification2: ISmsNotification = sampleWithPartialData;
        expectedResult = service.addSmsNotificationToCollectionIfMissing([], smsNotification, smsNotification2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(smsNotification);
        expect(expectedResult).toContain(smsNotification2);
      });

      it('should accept null and undefined values', () => {
        const smsNotification: ISmsNotification = sampleWithRequiredData;
        expectedResult = service.addSmsNotificationToCollectionIfMissing([], null, smsNotification, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(smsNotification);
      });

      it('should return initial array if no SmsNotification is added', () => {
        const smsNotificationCollection: ISmsNotification[] = [sampleWithRequiredData];
        expectedResult = service.addSmsNotificationToCollectionIfMissing(smsNotificationCollection, undefined, null);
        expect(expectedResult).toEqual(smsNotificationCollection);
      });
    });

    describe('compareSmsNotification', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSmsNotification(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 23662 };
        const entity2 = null;

        const compareResult1 = service.compareSmsNotification(entity1, entity2);
        const compareResult2 = service.compareSmsNotification(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 23662 };
        const entity2 = { id: 751 };

        const compareResult1 = service.compareSmsNotification(entity1, entity2);
        const compareResult2 = service.compareSmsNotification(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 23662 };
        const entity2 = { id: 23662 };

        const compareResult1 = service.compareSmsNotification(entity1, entity2);
        const compareResult2 = service.compareSmsNotification(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
