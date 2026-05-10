import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Contract } from '../models/contract.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class ContractService {
  private readonly http = inject(HttpClient);

  private url(projectId: string) { return `/projects/${projectId}/contracts`; }

  list(projectId: string, page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Contract>>(this.url(projectId), { params });
  }

  getById(projectId: string, id: string) {
    return this.http.get<Contract>(`${this.url(projectId)}/${id}`);
  }

  create(projectId: string, request: Partial<Contract>) {
    return this.http.post<Contract>(this.url(projectId), request);
  }

  update(projectId: string, id: string, request: Partial<Contract>) {
    return this.http.put<Contract>(`${this.url(projectId)}/${id}`, request);
  }

  addChangeOrder(projectId: string, contractId: string, changeOrder: any) {
    return this.http.post(`${this.url(projectId)}/${contractId}/change-orders`, changeOrder);
  }
}
