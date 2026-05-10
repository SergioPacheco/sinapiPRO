import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Payable, Receivable, CashFlowSummary, CashFlowMonth, BudgetVsActualLine } from '../models/finance.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class FinanceService {
  private readonly http = inject(HttpClient);

  private url(projectId: string) { return `/projects/${projectId}/finance`; }

  listPayables(projectId: string, page = 0, size = 20) {
    return this.http.get<PageResponse<Payable>>(`${this.url(projectId)}/payables`, { params: new HttpParams().set('page', page).set('size', size) });
  }

  listReceivables(projectId: string, page = 0, size = 20) {
    return this.http.get<PageResponse<Receivable>>(`${this.url(projectId)}/receivables`, { params: new HttpParams().set('page', page).set('size', size) });
  }

  overduePayables(projectId: string) {
    return this.http.get<Payable[]>(`${this.url(projectId)}/payables/overdue`);
  }

  overdueReceivables(projectId: string) {
    return this.http.get<Receivable[]>(`${this.url(projectId)}/receivables/overdue`);
  }

  createPayable(projectId: string, data: Partial<Payable>) {
    return this.http.post<Payable>(`${this.url(projectId)}/payables`, data);
  }

  createReceivable(projectId: string, data: Partial<Receivable>) {
    return this.http.post<Receivable>(`${this.url(projectId)}/receivables`, data);
  }

  payPayable(projectId: string, id: string, amount: number, date: string) {
    return this.http.post<Payable>(`${this.url(projectId)}/payables/${id}/pay`, { amount, date });
  }

  receivePayment(projectId: string, id: string, amount: number, date: string) {
    return this.http.post<Receivable>(`${this.url(projectId)}/receivables/${id}/receive`, { amount, date });
  }

  cashFlowSummary(projectId: string) {
    return this.http.get<CashFlowSummary>(`${this.url(projectId)}/cash-flow/summary`);
  }

  cashFlowProjection(projectId: string, startDate: string, endDate: string) {
    return this.http.get<{ months: CashFlowMonth[] }>(`${this.url(projectId)}/cash-flow/projection`, { params: new HttpParams().set('startDate', startDate).set('endDate', endDate) });
  }

  budgetVsActual(projectId: string) {
    return this.http.get<{ lines: BudgetVsActualLine[]; totals: BudgetVsActualLine }>(`${this.url(projectId)}/budget-vs-actual`);
  }
}
