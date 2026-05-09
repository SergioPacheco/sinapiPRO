import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { Measurement } from '../models/measurement.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class MeasurementService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.baseUrl}/measurements`;

  list(budgetId: string, page = 0, size = 20) {
    const params = new HttpParams().set('budgetId', budgetId).set('page', page).set('size', size);
    return this.http.get<PageResponse<Measurement>>(this.baseUrl, { params });
  }

  getById(id: string) {
    return this.http.get<Measurement>(`${this.baseUrl}/${id}`);
  }

  create(data: any) {
    return this.http.post<Measurement>(this.baseUrl, data);
  }

  submit(id: string) {
    return this.http.post<Measurement>(`${this.baseUrl}/${id}/submit`, {});
  }

  approve(id: string) {
    return this.http.post<Measurement>(`${this.baseUrl}/${id}/approve`, {});
  }
}
