import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import { ScheduleActivity, SCurveData } from '../models/schedule.model';

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

  physicalFinancialReportUrl(projectId: string) {
    return `${environment.baseUrl}${this.url(projectId)}/reports/physical-financial.pdf`;
  }
}
