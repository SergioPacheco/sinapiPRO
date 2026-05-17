import { ActivatedRoute, Router } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { DailyLogFormComponent } from './daily-log-form';
import { DailyLogService } from '../services/daily-log.service';

describe('DailyLogFormComponent', () => {
  let fixture: ComponentFixture<DailyLogFormComponent>;
  let component: DailyLogFormComponent;
  let service: jasmine.SpyObj<DailyLogService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    service = jasmine.createSpyObj<DailyLogService>('DailyLogService', ['create', 'recordWeatherDelay']);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    service.create.and.returnValue(of({
      id: 'd1',
      projectId: 'p1',
      logDate: '2026-05-16',
      laborCount: 0,
      equipmentCount: 0,
    } as any));
    service.recordWeatherDelay.and.returnValue(of({} as any));

    await TestBed.configureTestingModule({
      imports: [DailyLogFormComponent],
      providers: [
        { provide: DailyLogService, useValue: service },
        { provide: Router, useValue: router },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: new Map(),
              parent: {
                paramMap: new Map(),
                parent: { paramMap: new Map([['projectId', 'p1']]) },
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DailyLogFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create a daily log and navigate back without weather delay', () => {
    component.form.patchValue({
      logDate: '2026-05-16',
      weatherMorning: 'SUNNY',
      weatherAfternoon: 'CLOUDY',
      observations: 'Dia normal',
      weatherHoursLost: 0,
      weatherCondition: '',
      weatherImpact: '',
    });

    component.onSubmit();

    expect(service.create).toHaveBeenCalledWith('p1', {
      logDate: '2026-05-16',
      weatherMorning: 'SUNNY',
      weatherAfternoon: 'CLOUDY',
      observations: 'Dia normal',
      labor: [],
      equipment: [],
      occurrences: [],
    });
    expect(service.recordWeatherDelay).not.toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['../'], { relativeTo: jasmine.anything() });
  });

  it('should register weather delay after creating the daily log', () => {
    component.form.patchValue({
      logDate: '2026-05-16',
      weatherMorning: 'RAINY',
      weatherAfternoon: 'STORMY',
      observations: 'Chuva forte',
      weatherHoursLost: 4,
      weatherCondition: 'CHUVA_FORTE',
      weatherImpact: 'Paralisacao parcial',
    });

    component.onSubmit();

    expect(service.recordWeatherDelay).toHaveBeenCalledWith('p1', {
      delayDate: '2026-05-16',
      weatherCondition: 'CHUVA_FORTE',
      hoursLost: 4,
      impactDescription: 'Paralisacao parcial',
    });
    expect(router.navigate).toHaveBeenCalledWith(['../'], { relativeTo: jasmine.anything() });
  });
});
