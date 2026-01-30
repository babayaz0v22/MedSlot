import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IDoctorSchedule } from '../doctor-schedule.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../doctor-schedule.test-samples';

import { DoctorScheduleService, RestDoctorSchedule } from './doctor-schedule.service';

const requireRestSample: RestDoctorSchedule = {
  ...sampleWithRequiredData,
  startTime: sampleWithRequiredData.startTime?.toJSON(),
  endTime: sampleWithRequiredData.endTime?.toJSON(),
};

describe('DoctorSchedule Service', () => {
  let service: DoctorScheduleService;
  let httpMock: HttpTestingController;
  let expectedResult: IDoctorSchedule | IDoctorSchedule[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DoctorScheduleService);
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

    it('should create a DoctorSchedule', () => {
      const doctorSchedule = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(doctorSchedule).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DoctorSchedule', () => {
      const doctorSchedule = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(doctorSchedule).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DoctorSchedule', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DoctorSchedule', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DoctorSchedule', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDoctorScheduleToCollectionIfMissing', () => {
      it('should add a DoctorSchedule to an empty array', () => {
        const doctorSchedule: IDoctorSchedule = sampleWithRequiredData;
        expectedResult = service.addDoctorScheduleToCollectionIfMissing([], doctorSchedule);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(doctorSchedule);
      });

      it('should not add a DoctorSchedule to an array that contains it', () => {
        const doctorSchedule: IDoctorSchedule = sampleWithRequiredData;
        const doctorScheduleCollection: IDoctorSchedule[] = [
          {
            ...doctorSchedule,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDoctorScheduleToCollectionIfMissing(doctorScheduleCollection, doctorSchedule);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DoctorSchedule to an array that doesn't contain it", () => {
        const doctorSchedule: IDoctorSchedule = sampleWithRequiredData;
        const doctorScheduleCollection: IDoctorSchedule[] = [sampleWithPartialData];
        expectedResult = service.addDoctorScheduleToCollectionIfMissing(doctorScheduleCollection, doctorSchedule);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(doctorSchedule);
      });

      it('should add only unique DoctorSchedule to an array', () => {
        const doctorScheduleArray: IDoctorSchedule[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const doctorScheduleCollection: IDoctorSchedule[] = [sampleWithRequiredData];
        expectedResult = service.addDoctorScheduleToCollectionIfMissing(doctorScheduleCollection, ...doctorScheduleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const doctorSchedule: IDoctorSchedule = sampleWithRequiredData;
        const doctorSchedule2: IDoctorSchedule = sampleWithPartialData;
        expectedResult = service.addDoctorScheduleToCollectionIfMissing([], doctorSchedule, doctorSchedule2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(doctorSchedule);
        expect(expectedResult).toContain(doctorSchedule2);
      });

      it('should accept null and undefined values', () => {
        const doctorSchedule: IDoctorSchedule = sampleWithRequiredData;
        expectedResult = service.addDoctorScheduleToCollectionIfMissing([], null, doctorSchedule, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(doctorSchedule);
      });

      it('should return initial array if no DoctorSchedule is added', () => {
        const doctorScheduleCollection: IDoctorSchedule[] = [sampleWithRequiredData];
        expectedResult = service.addDoctorScheduleToCollectionIfMissing(doctorScheduleCollection, undefined, null);
        expect(expectedResult).toEqual(doctorScheduleCollection);
      });
    });

    describe('compareDoctorSchedule', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDoctorSchedule(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17031 };
        const entity2 = null;

        const compareResult1 = service.compareDoctorSchedule(entity1, entity2);
        const compareResult2 = service.compareDoctorSchedule(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17031 };
        const entity2 = { id: 16116 };

        const compareResult1 = service.compareDoctorSchedule(entity1, entity2);
        const compareResult2 = service.compareDoctorSchedule(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 17031 };
        const entity2 = { id: 17031 };

        const compareResult1 = service.compareDoctorSchedule(entity1, entity2);
        const compareResult2 = service.compareDoctorSchedule(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
