import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { AvailableMeasurementItem, Measurement } from '../models/measurement.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class MeasurementService {
  private readonly http = inject(HttpClient);

  private url(projectId: string) { return `/projects/${projectId}/measurements`; }

  list(projectId: string, page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Measurement>>(this.url(projectId), { params });
  }

  getById(projectId: string, id: string) {
    return this.http.get<Measurement>(`${this.url(projectId)}/${id}`);
  }

  create(projectId: string, data: any) {
    return this.http.post<Measurement>(this.url(projectId), data);
  }

  availableItems(projectId: string) {
    return this.http.get<AvailableMeasurementItem[]>(`${this.url(projectId)}/available-items`);
  }

  submit(projectId: string, id: string) {
    return this.http.post<Measurement>(`${this.url(projectId)}/${id}/submit`, {});
  }

  approve(projectId: string, id: string) {
    return this.http.post<Measurement>(`${this.url(projectId)}/${id}/approve`, {});
  }

  bulletinReportUrl(projectId: string, id: string) {
    return `${environment.baseUrl}${this.url(projectId)}/${id}/reports/bulletin.pdf`;
  }
}
