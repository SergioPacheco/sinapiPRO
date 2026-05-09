import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { DailyLog } from '../models/daily-log.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class DailyLogService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.baseUrl}/daily-logs`;

  list(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<DailyLog>>(this.baseUrl, { params });
  }

  create(request: Partial<DailyLog>) {
    return this.http.post<DailyLog>(this.baseUrl, request);
  }
}
