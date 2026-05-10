import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Budget, BudgetStatus, CreateBudgetRequest } from '../models/budget.model';

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

@Injectable({ providedIn: 'root' })
export class BudgetService {
  private readonly http = inject(HttpClient);

  private url(projectId: string) { return `/projects/${projectId}/budgets`; }

  list(projectId: string, page = 0, size = 20, status?: BudgetStatus) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<PageResponse<Budget>>(this.url(projectId), { params });
  }

  getById(projectId: string, id: string) {
    return this.http.get<Budget>(`${this.url(projectId)}/${id}`);
  }

  create(projectId: string, request: CreateBudgetRequest) {
    return this.http.post<Budget>(this.url(projectId), request);
  }

  update(projectId: string, id: string, request: Partial<CreateBudgetRequest>) {
    return this.http.put<Budget>(`${this.url(projectId)}/${id}`, request);
  }

  delete(projectId: string, id: string) {
    return this.http.delete<void>(`${this.url(projectId)}/${id}`);
  }

  copy(projectId: string, id: string, request: { code: string; title: string }) {
    return this.http.post<Budget>(`${this.url(projectId)}/${id}/copy`, request);
  }

  activate(projectId: string, id: string) {
    return this.http.post<Budget>(`${this.url(projectId)}/${id}/activate`, {});
  }
}
