import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { SmsNotificationDetailComponent } from './sms-notification-detail.component';

describe('SmsNotification Management Detail Component', () => {
  let comp: SmsNotificationDetailComponent;
  let fixture: ComponentFixture<SmsNotificationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SmsNotificationDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./sms-notification-detail.component').then(m => m.SmsNotificationDetailComponent),
              resolve: { smsNotification: () => of({ id: 23662 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SmsNotificationDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SmsNotificationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load smsNotification on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SmsNotificationDetailComponent);

      // THEN
      expect(instance.smsNotification()).toEqual(expect.objectContaining({ id: 23662 }));
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
