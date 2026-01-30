import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IClinic } from 'app/entities/clinic/clinic.model';
import { ClinicService } from 'app/entities/clinic/service/clinic.service';
import { PatientService } from '../service/patient.service';
import { IPatient } from '../patient.model';
import { PatientFormService } from './patient-form.service';

import { PatientUpdateComponent } from './patient-update.component';

describe('Patient Management Update Component', () => {
  let comp: PatientUpdateComponent;
  let fixture: ComponentFixture<PatientUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let patientFormService: PatientFormService;
  let patientService: PatientService;
  let clinicService: ClinicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PatientUpdateComponent],
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
      .overrideTemplate(PatientUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PatientUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    patientFormService = TestBed.inject(PatientFormService);
    patientService = TestBed.inject(PatientService);
    clinicService = TestBed.inject(ClinicService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Clinic query and add missing value', () => {
      const patient: IPatient = { id: 16914 };
      const clinic: IClinic = { id: 22299 };
      patient.clinic = clinic;

      const clinicCollection: IClinic[] = [{ id: 22299 }];
      jest.spyOn(clinicService, 'query').mockReturnValue(of(new HttpResponse({ body: clinicCollection })));
      const additionalClinics = [clinic];
      const expectedCollection: IClinic[] = [...additionalClinics, ...clinicCollection];
      jest.spyOn(clinicService, 'addClinicToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ patient });
      comp.ngOnInit();

      expect(clinicService.query).toHaveBeenCalled();
      expect(clinicService.addClinicToCollectionIfMissing).toHaveBeenCalledWith(
        clinicCollection,
        ...additionalClinics.map(expect.objectContaining),
      );
      expect(comp.clinicsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const patient: IPatient = { id: 16914 };
      const clinic: IClinic = { id: 22299 };
      patient.clinic = clinic;

      activatedRoute.data = of({ patient });
      comp.ngOnInit();

      expect(comp.clinicsSharedCollection).toContainEqual(clinic);
      expect(comp.patient).toEqual(patient);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPatient>>();
      const patient = { id: 16668 };
      jest.spyOn(patientFormService, 'getPatient').mockReturnValue(patient);
      jest.spyOn(patientService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ patient });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: patient }));
      saveSubject.complete();

      // THEN
      expect(patientFormService.getPatient).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(patientService.update).toHaveBeenCalledWith(expect.objectContaining(patient));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPatient>>();
      const patient = { id: 16668 };
      jest.spyOn(patientFormService, 'getPatient').mockReturnValue({ id: null });
      jest.spyOn(patientService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ patient: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: patient }));
      saveSubject.complete();

      // THEN
      expect(patientFormService.getPatient).toHaveBeenCalled();
      expect(patientService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPatient>>();
      const patient = { id: 16668 };
      jest.spyOn(patientService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ patient });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(patientService.update).toHaveBeenCalled();
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
