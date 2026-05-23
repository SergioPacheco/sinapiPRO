import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DailyLogService } from './daily-log.service';

describe('DailyLogService', () => {
  let service: DailyLogService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DailyLogService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(DailyLogService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should add labor entry', () => {
    const payload = { workerName: 'Joao', role: 'Pedreiro', hours: 8 };
    service.addLabor('p1', 'd1', payload).subscribe();

    const req = httpMock.expectOne('/projects/p1/daily-logs/d1/labor');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({});
  });

  it('should record weather delay', () => {
    const payload = { delayDate: '2026-05-01', weatherCondition: 'CHUVA_FORTE', hoursLost: 6, fullDayLost: false };
    service.recordWeatherDelay('p1', payload).subscribe();

    const req = httpMock.expectOne('/projects/p1/weather-delays');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({});
  });

  it('should list weather delay summary', () => {
    service.weatherDelaySummary('p1').subscribe();

    const req = httpMock.expectOne('/projects/p1/weather-delays/summary');
    expect(req.request.method).toBe('GET');
    req.flush({ totalDaysWithDelay: 0, totalHoursLost: 0, delaysByCondition: {} });
  });

  it('should return rdo report url', () => {
    expect(service.rdoReportUrl('p1', 'd1')).toBe('/api/v1/projects/p1/daily-logs/d1/reports/rdo.pdf');
  });
});
