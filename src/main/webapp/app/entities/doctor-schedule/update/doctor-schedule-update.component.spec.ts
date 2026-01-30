import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';
import { DoctorScheduleService } from '../service/doctor-schedule.service';
import { IDoctorSchedule } from '../doctor-schedule.model';
import { DoctorScheduleFormService } from './doctor-schedule-form.service';

import { DoctorScheduleUpdateComponent } from './doctor-schedule-update.component';

describe('DoctorSchedule Management Update Component', () => {
  let comp: DoctorScheduleUpdateComponent;
  let fixture: ComponentFixture<DoctorScheduleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let doctorScheduleFormService: DoctorScheduleFormService;
  let doctorScheduleService: DoctorScheduleService;
  let doctorService: DoctorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DoctorScheduleUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(DoctorScheduleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DoctorScheduleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    doctorScheduleFormService = TestBed.inject(DoctorScheduleFormService);
    doctorScheduleService = TestBed.inject(DoctorScheduleService);
    doctorService = TestBed.inject(DoctorService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Doctor query and add missing value', () => {
      const doctorSchedule: IDoctorSchedule = { id: 16116 };
      const doctor: IDoctor = { id: 758 };
      doctorSchedule.doctor = doctor;

      const doctorCollection: IDoctor[] = [{ id: 758 }];
      jest.spyOn(doctorService, 'query').mockReturnValue(of(new HttpResponse({ body: doctorCollection })));
      const additionalDoctors = [doctor];
      const expectedCollection: IDoctor[] = [...additionalDoctors, ...doctorCollection];
      jest.spyOn(doctorService, 'addDoctorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ doctorSchedule });
      comp.ngOnInit();

      expect(doctorService.query).toHaveBeenCalled();
      expect(doctorService.addDoctorToCollectionIfMissing).toHaveBeenCalledWith(
        doctorCollection,
        ...additionalDoctors.map(expect.objectContaining),
      );
      expect(comp.doctorsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const doctorSchedule: IDoctorSchedule = { id: 16116 };
      const doctor: IDoctor = { id: 758 };
      doctorSchedule.doctor = doctor;

      activatedRoute.data = of({ doctorSchedule });
      comp.ngOnInit();

      expect(comp.doctorsSharedCollection).toContainEqual(doctor);
      expect(comp.doctorSchedule).toEqual(doctorSchedule);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctorSchedule>>();
      const doctorSchedule = { id: 17031 };
      jest.spyOn(doctorScheduleFormService, 'getDoctorSchedule').mockReturnValue(doctorSchedule);
      jest.spyOn(doctorScheduleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctorSchedule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: doctorSchedule }));
      saveSubject.complete();

      // THEN
      expect(doctorScheduleFormService.getDoctorSchedule).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(doctorScheduleService.update).toHaveBeenCalledWith(expect.objectContaining(doctorSchedule));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctorSchedule>>();
      const doctorSchedule = { id: 17031 };
      jest.spyOn(doctorScheduleFormService, 'getDoctorSchedule').mockReturnValue({ id: null });
      jest.spyOn(doctorScheduleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctorSchedule: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: doctorSchedule }));
      saveSubject.complete();

      // THEN
      expect(doctorScheduleFormService.getDoctorSchedule).toHaveBeenCalled();
      expect(doctorScheduleService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctorSchedule>>();
      const doctorSchedule = { id: 17031 };
      jest.spyOn(doctorScheduleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctorSchedule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(doctorScheduleService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDoctor', () => {
      it('should forward to doctorService', () => {
        const entity = { id: 758 };
        const entity2 = { id: 23078 };
        jest.spyOn(doctorService, 'compareDoctor');
        comp.compareDoctor(entity, entity2);
        expect(doctorService.compareDoctor).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
