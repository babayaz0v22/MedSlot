import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IClinic } from 'app/entities/clinic/clinic.model';
import { ClinicService } from 'app/entities/clinic/service/clinic.service';
import { DoctorService } from '../service/doctor.service';
import { IDoctor } from '../doctor.model';
import { DoctorFormService } from './doctor-form.service';

import { DoctorUpdateComponent } from './doctor-update.component';

describe('Doctor Management Update Component', () => {
  let comp: DoctorUpdateComponent;
  let fixture: ComponentFixture<DoctorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let doctorFormService: DoctorFormService;
  let doctorService: DoctorService;
  let clinicService: ClinicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DoctorUpdateComponent],
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
      .overrideTemplate(DoctorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DoctorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    doctorFormService = TestBed.inject(DoctorFormService);
    doctorService = TestBed.inject(DoctorService);
    clinicService = TestBed.inject(ClinicService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Clinic query and add missing value', () => {
      const doctor: IDoctor = { id: 23078 };
      const clinic: IClinic = { id: 22299 };
      doctor.clinic = clinic;

      const clinicCollection: IClinic[] = [{ id: 22299 }];
      jest.spyOn(clinicService, 'query').mockReturnValue(of(new HttpResponse({ body: clinicCollection })));
      const additionalClinics = [clinic];
      const expectedCollection: IClinic[] = [...additionalClinics, ...clinicCollection];
      jest.spyOn(clinicService, 'addClinicToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      expect(clinicService.query).toHaveBeenCalled();
      expect(clinicService.addClinicToCollectionIfMissing).toHaveBeenCalledWith(
        clinicCollection,
        ...additionalClinics.map(expect.objectContaining),
      );
      expect(comp.clinicsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const doctor: IDoctor = { id: 23078 };
      const clinic: IClinic = { id: 22299 };
      doctor.clinic = clinic;

      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      expect(comp.clinicsSharedCollection).toContainEqual(clinic);
      expect(comp.doctor).toEqual(doctor);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 758 };
      jest.spyOn(doctorFormService, 'getDoctor').mockReturnValue(doctor);
      jest.spyOn(doctorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: doctor }));
      saveSubject.complete();

      // THEN
      expect(doctorFormService.getDoctor).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(doctorService.update).toHaveBeenCalledWith(expect.objectContaining(doctor));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 758 };
      jest.spyOn(doctorFormService, 'getDoctor').mockReturnValue({ id: null });
      jest.spyOn(doctorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: doctor }));
      saveSubject.complete();

      // THEN
      expect(doctorFormService.getDoctor).toHaveBeenCalled();
      expect(doctorService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 758 };
      jest.spyOn(doctorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(doctorService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareClinic', () => {
      it('should forward to clinicService', () => {
        const entity = { id: 22299 };
        const entity2 = { id: 8779 };
        jest.spyOn(clinicService, 'compareClinic');
        comp.compareClinic(entity, entity2);
        expect(clinicService.compareClinic).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
