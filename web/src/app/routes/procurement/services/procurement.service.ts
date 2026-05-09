import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { PurchaseOrder, Quotation, InventoryItem } from '../models/procurement.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class ProcurementService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.baseUrl}/procurement`;

  listOrders(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<PurchaseOrder>>(`${this.baseUrl}/orders`, { params });
  }

  listQuotations(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Quotation>>(`${this.baseUrl}/quotations`, { params });
  }

  listInventory(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<InventoryItem>>(`${this.baseUrl}/inventory`, { params });
  }

  receive(orderId: string, data: any) {
    return this.http.post(`${this.baseUrl}/orders/${orderId}/receive`, data);
  }
}
