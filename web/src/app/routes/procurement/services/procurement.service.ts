import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PurchaseOrder, Quotation, InventoryItem } from '../models/procurement.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class ProcurementService {
  private readonly http = inject(HttpClient);

  private url(projectId: string) { return `/projects/${projectId}/procurement`; }

  listOrders(projectId: string, page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<PurchaseOrder>>(`${this.url(projectId)}/orders`, { params });
  }

  listQuotations(projectId: string, page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Quotation>>(`${this.url(projectId)}/quotations`, { params });
  }

  listInventory(projectId: string, page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<InventoryItem>>(`${this.url(projectId)}/inventory`, { params });
  }

  receive(projectId: string, orderId: string, data: any) {
    return this.http.post(`${this.url(projectId)}/orders/${orderId}/receive`, data);
  }
}
