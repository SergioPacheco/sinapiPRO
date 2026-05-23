import { ActivatedRoute, Router } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { DailyLogListComponent } from './daily-log-list';
import { DailyLogService } from '../services/daily-log.service';

describe('DailyLogListComponent', () => {
  let fixture: ComponentFixture<DailyLogListComponent>;
  let component: DailyLogListComponent;
  let service: jasmine.SpyObj<DailyLogService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    service = jasmine.createSpyObj<DailyLogService>('DailyLogService', [
      'list',
      'weatherDelaySummary',
      'rdoReportUrl',
    ]);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    service.list.and.returnValue(of({ content: [], totalElements: 0 } as any));
    service.weatherDelaySummary.and.returnValue(of({ totalDelays: 0, fullDaysLost: 0, totalHoursLost: 0 } as any));
    service.rdoReportUrl.and.returnValue('/rdo.pdf');

    await TestBed.configureTestingModule({
      imports: [DailyLogListComponent],
      providers: [
        { provide: DailyLogService, useValue: service },
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

    fixture = TestBed.createComponent(DailyLogListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load logs and weather summary on init', () => {
    expect(service.list).toHaveBeenCalledWith('p1', 0, 20);
    expect(service.weatherDelaySummary).toHaveBeenCalledWith('p1');
  });

  it('should open rdo report by service url', () => {
    const openSpy = spyOn(window, 'open');

    component.downloadRdo({ id: 'd1', logDate: '2026-05-17' } as any);

    expect(service.rdoReportUrl).toHaveBeenCalledWith('p1', 'd1');
    expect(openSpy).toHaveBeenCalledWith('/rdo.pdf', '_blank');
  });

  it('should navigate to detail', () => {
    component.openDetail({ id: 'd1' } as any);

    expect(router.navigate).toHaveBeenCalledWith(['d1'], { relativeTo: jasmine.anything() });
  });
});
