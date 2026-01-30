import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DoctorScheduleDetailComponent } from './doctor-schedule-detail.component';

describe('DoctorSchedule Management Detail Component', () => {
  let comp: DoctorScheduleDetailComponent;
  let fixture: ComponentFixture<DoctorScheduleDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DoctorScheduleDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./doctor-schedule-detail.component').then(m => m.DoctorScheduleDetailComponent),
              resolve: { doctorSchedule: () => of({ id: 17031 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DoctorScheduleDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DoctorScheduleDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load doctorSchedule on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DoctorScheduleDetailComponent);

      // THEN
      expect(instance.doctorSchedule()).toEqual(expect.objectContaining({ id: 17031 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
