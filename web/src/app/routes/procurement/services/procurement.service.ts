import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { PurchaseOrder, Quotation } from '../models/procurement.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class ProcurementService {
  private readonly http = inject(HttpClient);

  private url(projectId: string) { return `/projects/${projectId}/procurement`; }

  listOrders(projectId: string, page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<PurchaseOrder>>(`${this.url(projectId)}/orders`, { params });
  }

  listQuotations(projectId: string, page = 0, size = 20, orderId?: string) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (orderId) params = params.set('orderId', orderId);
    return this.http.get<PageResponse<Quotation>>(`${this.url(projectId)}/quotations`, { params });
  }

  receive(projectId: string, orderId: string, data: any) {
    return this.http.post(`${this.url(projectId)}/orders/${orderId}/receive`, data);
  }

  listOverdue(projectId: string) {
    return this.http.get<PurchaseOrder[]>(`${this.url(projectId)}/orders/overdue`);
  }

  sendQuotationEmail(projectId: string, quotationId: string) {
    return this.http.post<{ sent: number; quotationId: string }>(`${this.url(projectId)}/quotations/${quotationId}/send-email`, {});
  }

  comparativeMapUrl(projectId: string, quotationId: string) {
    return `${environment.baseUrl}${this.url(projectId)}/quotations/${quotationId}/reports/comparative-map.pdf`;
  }

  orderReportUrl(projectId: string, orderId: string) {
    return `${environment.baseUrl}${this.url(projectId)}/orders/${orderId}/reports/order.pdf`;
  }

  generateFromAbc(projectId: string, items: { description: string; quantity: number; unit: string }[]) {
    return this.http.post(`${this.url(projectId)}/from-abc`, { items });
  }
}
