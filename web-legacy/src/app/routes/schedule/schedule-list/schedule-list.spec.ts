import { ActivatedRoute, Router } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { ScheduleListComponent } from './schedule-list';
import { ScheduleService } from '../services/schedule.service';

describe('ScheduleListComponent', () => {
  let fixture: ComponentFixture<ScheduleListComponent>;
  let component: ScheduleListComponent;
  let service: jasmine.SpyObj<ScheduleService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    service = jasmine.createSpyObj<ScheduleService>('ScheduleService', [
      'listActivities',
      'getSCurve',
      'getTracking',
      'listHolidays',
      'listBaselines',
      'createBaseline',
      'addHoliday',
      'distributeDates',
      'physicalFinancialReportUrl',
    ]);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    service.listActivities.and.returnValue(of([
      {
        id: 'a1',
        name: 'Fundacao',
        plannedStart: '2026-05-01',
        plannedEnd: '2026-05-10',
        weight: 10,
        progressPct: 30,
        sortOrder: 1,
      },
    ] as any));
    service.getSCurve.and.returnValue(of({ points: [] } as any));
    service.getTracking.and.returnValue(of([]));
    service.listHolidays.and.returnValue(of([]));
    service.listBaselines.and.returnValue(of([]));
    service.createBaseline.and.returnValue(of({} as any));
    service.addHoliday.and.returnValue(of({} as any));
    service.distributeDates.and.returnValue(of([] as any));
    service.physicalFinancialReportUrl.and.returnValue('/report.pdf');

    await TestBed.configureTestingModule({
      imports: [ScheduleListComponent],
      providers: [
        { provide: ScheduleService, useValue: service },
        { provide: Router, useValue: router },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: new Map() },
            parent: { snapshot: { paramMap: new Map([['projectId', 'p1']]) } },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ScheduleListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load planning datasets on init', () => {
    expect(service.listActivities).toHaveBeenCalledWith('p1');
    expect(service.getSCurve).toHaveBeenCalledWith('p1');
    expect(service.getTracking).toHaveBeenCalledWith('p1');
    expect(service.listHolidays).toHaveBeenCalledWith('p1');
    expect(service.listBaselines).toHaveBeenCalledWith('p1');
    expect(component.activities().length).toBe(1);
  });

  it('should save baseline and reload data', () => {
    component.baselineName = 'Baseline Inicial';

    component.saveBaseline();

    expect(service.createBaseline).toHaveBeenCalledWith('p1', 'Baseline Inicial');
    expect(component.baselineName).toBe('');
    expect(service.listActivities).toHaveBeenCalledTimes(2);
  });

  it('should add holiday and reset form', () => {
    component.holidayForm = { date: '2026-12-25', description: 'Natal', recurring: true };

    component.addHoliday();

    expect(service.addHoliday).toHaveBeenCalledWith('p1', {
      date: '2026-12-25',
      description: 'Natal',
      recurring: true,
    });
    expect(component.holidayForm).toEqual({ date: '', description: '', recurring: false });
  });

  it('should distribute dates when start date is present', () => {
    component.distributionStart = '2026-05-20';

    component.distributeDates();

    expect(service.distributeDates).toHaveBeenCalledWith('p1', '2026-05-20');
  });
});
