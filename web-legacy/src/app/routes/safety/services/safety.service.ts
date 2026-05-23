import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SafetyIncident } from '../models/safety.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class SafetyService {
  private readonly http = inject(HttpClient);

  listTemplates(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<any>>('/safety/templates', { params });
  }

  listIncidents(projectId: string, page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<SafetyIncident>>(`/projects/${projectId}/safety/incidents`, { params });
  }
}
