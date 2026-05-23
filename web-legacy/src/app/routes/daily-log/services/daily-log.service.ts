import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { CreateDailyLogRequest, DailyLog, DailyLogDetail, WeatherDelay, WeatherDelaySummary } from '../models/daily-log.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class DailyLogService {
  private readonly http = inject(HttpClient);

  private url(projectId: string) { return `/projects/${projectId}/daily-logs`; }

  list(projectId: string, page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<DailyLog>>(this.url(projectId), { params });
  }

  create(projectId: string, request: CreateDailyLogRequest) {
    return this.http.post<DailyLog>(this.url(projectId), request);
  }

  detail(projectId: string, id: string) {
    return this.http.get<DailyLogDetail>(`${this.url(projectId)}/${id}`);
  }

  addLabor(projectId: string, id: string, request: { workerName: string; role: string; hours: number }) {
    return this.http.post(`${this.url(projectId)}/${id}/labor`, request);
  }

  addEquipment(projectId: string, id: string, request: { equipmentName: string; hoursUsed: number; hoursIdle?: number }) {
    return this.http.post(`${this.url(projectId)}/${id}/equipment`, request);
  }

  addOccurrence(projectId: string, id: string, request: { type: string; description: string }) {
    return this.http.post(`${this.url(projectId)}/${id}/occurrences`, request);
  }

  addPhoto(projectId: string, id: string, request: { filePath: string; caption?: string }) {
    return this.http.post(`${this.url(projectId)}/${id}/photos`, request);
  }

  listWeatherDelays(projectId: string) {
    return this.http.get<WeatherDelay[]>(`/projects/${projectId}/weather-delays`);
  }

  weatherDelaySummary(projectId: string) {
    return this.http.get<WeatherDelaySummary>(`/projects/${projectId}/weather-delays/summary`);
  }

  recordWeatherDelay(projectId: string, request: { delayDate: string; weatherCondition: string; hoursLost: number; fullDayLost?: boolean; impactDescription?: string; reportedBy?: string }) {
    return this.http.post(`/projects/${projectId}/weather-delays`, request);
  }

  rdoReportUrl(projectId: string, id: string) {
    return `${environment.baseUrl}${this.url(projectId)}/${id}/reports/rdo.pdf`;
  }
}
