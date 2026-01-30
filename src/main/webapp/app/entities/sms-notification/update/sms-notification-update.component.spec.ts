import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IClinic } from 'app/entities/clinic/clinic.model';
import { ClinicService } from 'app/entities/clinic/service/clinic.service';
import { IAppointment } from 'app/entities/appointment/appointment.model';
import { AppointmentService } from 'app/entities/appointment/service/appointment.service';
import { ISmsNotification } from '../sms-notification.model';
import { SmsNotificationService } from '../service/sms-notification.service';
import { SmsNotificationFormService } from './sms-notification-form.service';

import { SmsNotificationUpdateComponent } from './sms-notification-update.component';

describe('SmsNotification Management Update Component', () => {
  let comp: SmsNotificationUpdateComponent;
  let fixture: ComponentFixture<SmsNotificationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let smsNotificationFormService: SmsNotificationFormService;
  let smsNotificationService: SmsNotificationService;
  let clinicService: ClinicService;
  let appointmentService: AppointmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SmsNotificationUpdateComponent],
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
      .overrideTemplate(SmsNotificationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SmsNotificationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    smsNotificationFormService = TestBed.inject(SmsNotificationFormService);
    smsNotificationService = TestBed.inject(SmsNotificationService);
    clinicService = TestBed.inject(ClinicService);
    appointmentService = TestBed.inject(AppointmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Clinic query and add missing value', () => {
      const smsNotification: ISmsNotification = { id: 751 };
      const clinic: IClinic = { id: 22299 };
      smsNotification.clinic = clinic;

      const clinicCollection: IClinic[] = [{ id: 22299 }];
      jest.spyOn(clinicService, 'query').mockReturnValue(of(new HttpResponse({ body: clinicCollection })));
      const additionalClinics = [clinic];
      const expectedCollection: IClinic[] = [...additionalClinics, ...clinicCollection];
      jest.spyOn(clinicService, 'addClinicToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ smsNotification });
      comp.ngOnInit();

      expect(clinicService.query).toHaveBeenCalled();
      expect(clinicService.addClinicToCollectionIfMissing).toHaveBeenCalledWith(
        clinicCollection,
        ...additionalClinics.map(expect.objectContaining),
      );
      expect(comp.clinicsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Appointment query and add missing value', () => {
      const smsNotification: ISmsNotification = { id: 751 };
      const appointment: IAppointment = { id: 3011 };
      smsNotification.appointment = appointment;

      const appointmentCollection: IAppointment[] = [{ id: 3011 }];
      jest.spyOn(appointmentService, 'query').mockReturnValue(of(new HttpResponse({ body: appointmentCollection })));
      const additionalAppointments = [appointment];
      const expectedCollection: IAppointment[] = [...additionalAppointments, ...appointmentCollection];
      jest.spyOn(appointmentService, 'addAppointmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ smsNotification });
      comp.ngOnInit();

      expect(appointmentService.query).toHaveBeenCalled();
      expect(appointmentService.addAppointmentToCollectionIfMissing).toHaveBeenCalledWith(
        appointmentCollection,
        ...additionalAppointments.map(expect.objectContaining),
      );
      expect(comp.appointmentsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const smsNotification: ISmsNotification = { id: 751 };
      const clinic: IClinic = { id: 22299 };
      smsNotification.clinic = clinic;
      const appointment: IAppointment = { id: 3011 };
      smsNotification.appointment = appointment;

      activatedRoute.data = of({ smsNotification });
      comp.ngOnInit();

      expect(comp.clinicsSharedCollection).toContainEqual(clinic);
      expect(comp.appointmentsSharedCollection).toContainEqual(appointment);
      expect(comp.smsNotification).toEqual(smsNotification);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISmsNotification>>();
      const smsNotification = { id: 23662 };
      jest.spyOn(smsNotificationFormService, 'getSmsNotification').mockReturnValue(smsNotification);
      jest.spyOn(smsNotificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ smsNotification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: smsNotification }));
      saveSubject.complete();

      // THEN
      expect(smsNotificationFormService.getSmsNotification).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(smsNotificationService.update).toHaveBeenCalledWith(expect.objectContaining(smsNotification));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISmsNotification>>();
      const smsNotification = { id: 23662 };
      jest.spyOn(smsNotificationFormService, 'getSmsNotification').mockReturnValue({ id: null });
      jest.spyOn(smsNotificationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ smsNotification: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: smsNotification }));
      saveSubject.complete();

      // THEN
      expect(smsNotificationFormService.getSmsNotification).toHaveBeenCalled();
      expect(smsNotificationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISmsNotification>>();
      const smsNotification = { id: 23662 };
      jest.spyOn(smsNotificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ smsNotification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(smsNotificationService.update).toHaveBeenCalled();
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

    describe('compareAppointment', () => {
      it('should forward to appointmentService', () => {
        const entity = { id: 3011 };
        const entity2 = { id: 584 };
        jest.spyOn(appointmentService, 'compareAppointment');
        comp.compareAppointment(entity, entity2);
        expect(appointmentService.compareAppointment).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
