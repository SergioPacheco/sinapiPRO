import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { DailyLog } from '../models/daily-log.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class DailyLogService {
  private readonly http = inject(HttpClient);

  private url(projectId: string) { return `/projects/${projectId}/daily-logs`; }

  list(projectId: string, page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<DailyLog>>(this.url(projectId), { params });
  }

  create(projectId: string, request: Partial<DailyLog>) {
    return this.http.post<DailyLog>(this.url(projectId), request);
  }
}
