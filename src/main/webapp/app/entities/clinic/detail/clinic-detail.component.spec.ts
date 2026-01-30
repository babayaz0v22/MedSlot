import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ClinicDetailComponent } from './clinic-detail.component';

describe('Clinic Management Detail Component', () => {
  let comp: ClinicDetailComponent;
  let fixture: ComponentFixture<ClinicDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClinicDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./clinic-detail.component').then(m => m.ClinicDetailComponent),
              resolve: { clinic: () => of({ id: 22299 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ClinicDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClinicDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load clinic on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ClinicDetailComponent);

      // THEN
      expect(instance.clinic()).toEqual(expect.objectContaining({ id: 22299 }));
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
