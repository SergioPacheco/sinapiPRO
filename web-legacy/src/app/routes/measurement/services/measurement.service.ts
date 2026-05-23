import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { AvailableMeasurementItem, Measurement, MeasurementDetail, MeasurementHistoryEntry } from '../models/measurement.model';
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

  detail(projectId: string, id: string) {
    return this.http.get<MeasurementDetail>(`${this.url(projectId)}/${id}/detail`);
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

  reject(projectId: string, id: string, reason: string) {
    return this.http.post<Measurement>(`${this.url(projectId)}/${id}/reject`, { reason });
  }

  history(projectId: string, id: string) {
    return this.http.get<MeasurementHistoryEntry[]>(`${this.url(projectId)}/${id}/history`);
  }

  uploadAttachment(projectId: string, measurementId: string, file: File, title: string, uploadedBy?: string) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    formData.append('entityType', 'MEASUREMENT');
    formData.append('entityId', measurementId);
    if (uploadedBy) formData.append('uploadedBy', uploadedBy);
    return this.http.post(`/projects/${projectId}/documents`, formData);
  }

  memoUrl(projectId: string, measurementId: string, itemId: string) {
    return `${environment.baseUrl}/projects/${projectId}/measurements/${measurementId}/items/${itemId}/memo`;
  }

  getMemo(projectId: string, measurementId: string, itemId: string) {
    return this.http.get<{ measurementItemId: string; lines: { description: string; formula: string; value: number }[]; result: number }>(
      `${this.url(projectId)}/${measurementId}/items/${itemId}/memo`
    );
  }

  saveMemo(projectId: string, measurementId: string, itemId: string, lines: { description: string; formula: string; value: number }[], result?: number) {
    return this.http.put(
      `${this.url(projectId)}/${measurementId}/items/${itemId}/memo`,
      { lines, result }
    );
  }

  addExtraItem(projectId: string, measurementId: string, request: { description: string; quantity: number; unitPrice: number; contractorName?: string }) {
    return this.http.post<Measurement>(`${this.url(projectId)}/${measurementId}/extra-items`, request);
  }

  bulletinReportUrl(projectId: string, id: string) {
    return `${environment.baseUrl}${this.url(projectId)}/${id}/reports/bulletin.pdf`;
  }

  documentsUrl(projectId: string) {
    return `${environment.baseUrl}/projects/${projectId}/documents`;
  }
}
