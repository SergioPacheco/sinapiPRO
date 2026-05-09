import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
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
  private readonly baseUrl = `${environment.baseUrl}/budgets`;

  list(page = 0, size = 20, status?: BudgetStatus) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<PageResponse<Budget>>(this.baseUrl, { params });
  }

  getById(id: string) {
    return this.http.get<Budget>(`${this.baseUrl}/${id}`);
  }

  create(request: CreateBudgetRequest) {
    return this.http.post<Budget>(this.baseUrl, request);
  }

  update(id: string, request: Partial<CreateBudgetRequest>) {
    return this.http.put<Budget>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: string) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
