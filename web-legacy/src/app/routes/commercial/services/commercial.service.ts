import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Development, DevelopmentUnit, SalesProposal, BrokerCommission } from '../models/commercial.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class CommercialService {
  private readonly http = inject(HttpClient);
  private readonly base = '/commercial';

  listDevelopments(page = 0, size = 20) {
    return this.http.get<PageResponse<Development>>(`${this.base}/developments`, { params: new HttpParams().set('page', page).set('size', size) });
  }

  createDevelopment(data: Partial<Development>) {
    return this.http.post<Development>(`${this.base}/developments`, data);
  }

  listUnits(devId: string) {
    return this.http.get<DevelopmentUnit[]>(`${this.base}/developments/${devId}/units`);
  }

  availableUnits(devId: string) {
    return this.http.get<DevelopmentUnit[]>(`${this.base}/developments/${devId}/units/available`);
  }

  createUnit(devId: string, data: Partial<DevelopmentUnit>) {
    return this.http.post<DevelopmentUnit>(`${this.base}/developments/${devId}/units`, data);
  }

  listProposals(devId: string, page = 0, size = 20) {
    return this.http.get<PageResponse<SalesProposal>>(`${this.base}/developments/${devId}/proposals`, { params: new HttpParams().set('page', page).set('size', size) });
  }

  createProposal(data: any) {
    return this.http.post<SalesProposal>(`${this.base}/proposals`, data);
  }

  approveProposal(id: string) { return this.http.post<SalesProposal>(`${this.base}/proposals/${id}/approve`, {}); }
  signProposal(id: string) { return this.http.post<SalesProposal>(`${this.base}/proposals/${id}/sign`, {}); }
  rejectProposal(id: string) { return this.http.post<SalesProposal>(`${this.base}/proposals/${id}/reject`, {}); }

  listCommissions(proposalId: string) {
    return this.http.get<BrokerCommission[]>(`${this.base}/proposals/${proposalId}/commissions`);
  }

  pendingCommissions() {
    return this.http.get<BrokerCommission[]>(`${this.base}/commissions/pending`);
  }
}
