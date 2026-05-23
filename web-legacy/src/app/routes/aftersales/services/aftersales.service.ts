import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ServiceTicket, TicketSummary } from '../models/aftersales.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class AfterSalesService {
  private readonly http = inject(HttpClient);
  private readonly base = '/after-sales/tickets';

  list(status?: string, page = 0, size = 20) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<PageResponse<ServiceTicket>>(this.base, { params });
  }

  get(id: string) { return this.http.get<ServiceTicket>(`${this.base}/${id}`); }

  create(data: Partial<ServiceTicket>) { return this.http.post<ServiceTicket>(this.base, data); }

  assign(id: string, assignedTo: string) { return this.http.post<ServiceTicket>(`${this.base}/${id}/assign`, { assignedTo }); }

  resolve(id: string, resolution: string) { return this.http.post<ServiceTicket>(`${this.base}/${id}/resolve`, { resolution }); }

  close(id: string) { return this.http.post<ServiceTicket>(`${this.base}/${id}/close`, {}); }

  summary() { return this.http.get<TicketSummary>(`${this.base}/summary`); }
}
