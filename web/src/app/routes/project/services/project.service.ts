import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PageResponse } from '../../budget/services/budget.service';

export interface Project {
  id: string;
  code: string;
  name: string;
  description: string;
  customerName: string;
  customerDocument: string;
  address: string;
  city: string;
  state: string;
  responsibleEngineer: string;
  artNumber: string;
  startDate: string;
  expectedEndDate: string;
  actualEndDate: string;
  status: string;
  totalArea: number;
  totalBudget: number;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private readonly http = inject(HttpClient);

  list(page = 0, size = 20, q?: string, status?: string) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (q) params = params.set('q', q);
    if (status) params = params.set('status', status);
    return this.http.get<PageResponse<Project>>('/projects', { params });
  }

  getById(id: string) {
    return this.http.get<Project>(`/projects/${id}`);
  }

  create(data: Partial<Project>) {
    return this.http.post<Project>('/projects', data);
  }

  update(id: string, data: Partial<Project>) {
    return this.http.put<Project>(`/projects/${id}`, data);
  }

  updateStatus(id: string, status: string) {
    return this.http.patch<Project>(`/projects/${id}/status`, { status });
  }

  delete(id: string) {
    return this.http.delete<void>(`/projects/${id}`);
  }
}
