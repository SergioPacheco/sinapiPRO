import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { Contract } from '../models/contract.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class ContractService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.baseUrl}/contracts`;

  list(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Contract>>(this.baseUrl, { params });
  }

  getById(id: string) {
    return this.http.get<Contract>(`${this.baseUrl}/${id}`);
  }

  create(request: Partial<Contract>) {
    return this.http.post<Contract>(this.baseUrl, request);
  }

  update(id: string, request: Partial<Contract>) {
    return this.http.put<Contract>(`${this.baseUrl}/${id}`, request);
  }

  addChangeOrder(contractId: string, changeOrder: any) {
    return this.http.post(`${this.baseUrl}/${contractId}/change-orders`, changeOrder);
  }
}
