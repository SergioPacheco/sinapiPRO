import { ActivatedRoute, Router } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { DailyLogDetailComponent } from './daily-log-detail';
import { DailyLogService } from '../services/daily-log.service';

describe('DailyLogDetailComponent', () => {
  let fixture: ComponentFixture<DailyLogDetailComponent>;
  let component: DailyLogDetailComponent;
  let service: jasmine.SpyObj<DailyLogService>;

  beforeEach(async () => {
    service = jasmine.createSpyObj<DailyLogService>('DailyLogService', [
      'detail',
      'listWeatherDelays',
      'weatherDelaySummary',
      'addLabor',
      'addEquipment',
      'addOccurrence',
      'addPhoto',
      'recordWeatherDelay',
    ]);

    service.detail.and.returnValue(of({
      id: 'd1',
      logDate: '2026-05-01',
      labor: [],
      equipment: [],
      occurrences: [],
      photos: [],
    } as any));
    service.listWeatherDelays.and.returnValue(of([]));
    service.weatherDelaySummary.and.returnValue(of({ totalDelays: 0, fullDaysLost: 0, totalHoursLost: 0 } as any));
    service.addLabor.and.returnValue(of({} as any));
    service.addEquipment.and.returnValue(of({} as any));
    service.addOccurrence.and.returnValue(of({} as any));
    service.addPhoto.and.returnValue(of({} as any));
    service.recordWeatherDelay.and.returnValue(of({} as any));

    await TestBed.configureTestingModule({
      imports: [DailyLogDetailComponent],
      providers: [
        { provide: DailyLogService, useValue: service },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: new Map([['id', 'd1']]) },
            parent: { snapshot: { paramMap: new Map([['projectId', 'p1']]) } },
          },
        },
        { provide: Router, useValue: jasmine.createSpyObj<Router>('Router', ['navigate']) },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DailyLogDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load detail and weather data on init', () => {
    expect(service.detail).toHaveBeenCalledWith('p1', 'd1');
    expect(service.listWeatherDelays).toHaveBeenCalledWith('p1');
    expect(service.weatherDelaySummary).toHaveBeenCalledWith('p1');
  });

  it('should add labor and refresh detail', () => {
    component.newLabor = { workerName: 'Joao', role: 'Pedreiro', hours: 8 };

    component.addLabor();

    expect(service.addLabor).toHaveBeenCalledWith('p1', 'd1', { workerName: 'Joao', role: 'Pedreiro', hours: 8 });
    expect(service.detail).toHaveBeenCalledTimes(2);
  });

  it('should add weather delay and refresh weather summary', () => {
    component.newWeatherDelay = {
      delayDate: '2026-05-02',
      weatherCondition: 'CHUVA_FORTE',
      hoursLost: 4,
      fullDayLost: false,
      impactDescription: 'Paralisacao parcial',
    };

    component.addWeatherDelay();

    expect(service.recordWeatherDelay).toHaveBeenCalledWith('p1', component.newWeatherDelay);
    expect(service.listWeatherDelays).toHaveBeenCalledTimes(2);
    expect(service.weatherDelaySummary).toHaveBeenCalledTimes(2);
  });
});
