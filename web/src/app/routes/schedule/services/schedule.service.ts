import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import { Holiday, ScheduleActivity, SCurveData, ScheduleBaseline, ScheduleTrackingLine } from '../models/schedule.model';

@Injectable({ providedIn: 'root' })
export class ScheduleService {
  private readonly http = inject(HttpClient);

  private url(projectId: string) { return `/projects/${projectId}/schedule`; }

  listActivities(projectId: string) {
    return this.http.get<ScheduleActivity[]>(this.url(projectId));
  }

  getSCurve(projectId: string) {
    return this.http.get<SCurveData>(`${this.url(projectId)}/s-curve`);
  }

  createActivity(projectId: string, request: {
    name: string;
    plannedStart: string;
    plannedEnd: string;
    weight: number;
    sortOrder: number;
  }) {
    return this.http.post<ScheduleActivity>(this.url(projectId), request);
  }

  getCriticalPath(projectId: string) {
    return this.http.get(`${this.url(projectId)}/critical-path`);
  }

  listHolidays(projectId: string) {
    return this.http.get<Holiday[]>(`${this.url(projectId)}/holidays`);
  }

  addHoliday(projectId: string, request: { date: string; description: string; recurring: boolean }) {
    return this.http.post<Holiday>(`${this.url(projectId)}/holidays`, request);
  }

  listBaselines(projectId: string) {
    return this.http.get<ScheduleBaseline[]>(`${this.url(projectId)}/baselines`);
  }

  createBaseline(projectId: string, name: string) {
    return this.http.post<ScheduleBaseline>(`${this.url(projectId)}/baselines`, { name });
  }

  getTracking(projectId: string) {
    return this.http.get<ScheduleTrackingLine[]>(`${this.url(projectId)}/tracking`);
  }

  distributeDates(projectId: string, startDate: string) {
    return this.http.post<ScheduleActivity[]>(`${this.url(projectId)}/distribute-dates`, { startDate });
  }

  physicalFinancialReportUrl(projectId: string) {
    return `${environment.baseUrl}${this.url(projectId)}/reports/physical-financial.pdf`;
  }
}
