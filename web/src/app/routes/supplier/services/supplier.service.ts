import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { Supplier } from '../models/supplier.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class SupplierService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.baseUrl}/suppliers`;

  list(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Supplier>>(this.baseUrl, { params });
  }

  getById(id: string) {
    return this.http.get<Supplier>(`${this.baseUrl}/${id}`);
  }

  create(supplier: Partial<Supplier>) {
    return this.http.post<Supplier>(this.baseUrl, supplier);
  }

  update(id: string, supplier: Partial<Supplier>) {
    return this.http.put<Supplier>(`${this.baseUrl}/${id}`, supplier);
  }

  delete(id: string) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
