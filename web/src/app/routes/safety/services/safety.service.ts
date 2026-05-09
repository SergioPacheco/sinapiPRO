import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { SafetyIncident } from '../models/safety.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class SafetyService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.baseUrl}/safety/incidents`;

  listIncidents(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<SafetyIncident>>(this.baseUrl, { params });
  }
}
