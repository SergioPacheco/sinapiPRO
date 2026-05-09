import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { ScheduleActivity } from '../models/schedule.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class ScheduleService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.baseUrl}/schedule`;

  listActivities(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<ScheduleActivity>>(`${this.baseUrl}/activities`, { params });
  }

  getCriticalPath() {
    return this.http.get<ScheduleActivity[]>(`${this.baseUrl}/critical-path`);
  }
}
