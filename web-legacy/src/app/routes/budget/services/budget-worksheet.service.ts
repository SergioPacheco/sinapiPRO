import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import {
  BudgetBaseDateUpdateRequest,
  BudgetBdiConfig,
  BudgetBdiRequest,
  BudgetItemMemo,
  BudgetMemoLine,
  BudgetServiceAbcEntry,
  BudgetWorksheet,
} from '../models/budget.model';

@Injectable({ providedIn: 'root' })
export class BudgetWorksheetService {
  private readonly http = inject(HttpClient);

  worksheet(budgetId: string) {
    return this.http.get<BudgetWorksheet>(`/budgets/${budgetId}/worksheet`);
  }

  serviceAbcCurve(budgetId: string) {
    return this.http.get<BudgetServiceAbcEntry[]>(`/budgets/${budgetId}/abc-curve/services`);
  }

  getBdi(budgetId: string, itemType: string) {
    return this.http.get<BudgetBdiConfig>(`/budgets/${budgetId}/bdi`, { params: { itemType } });
  }

  setBdi(budgetId: string, request: BudgetBdiRequest) {
    return this.http.put<BudgetBdiConfig>(`/budgets/${budgetId}/bdi`, request);
  }

  updateBaseDate(budgetId: string, request: BudgetBaseDateUpdateRequest) {
    return this.http.post(`/budgets/${budgetId}/update-base-date`, request);
  }

  createStageEntry(budgetId: string, request: { name: string; sortOrder: number; parentId?: string }) {
    return this.http.post(`/budgets/${budgetId}/stages`, request);
  }

  addStageItem(budgetId: string, stageId: string, request: { compositionId: string; quantity: number }) {
    return this.http.post(`/budgets/${budgetId}/stages/${stageId}/items`, request);
  }

  getItemMemo(budgetId: string, itemId: string) {
    return this.http.get<BudgetItemMemo>(`/budgets/${budgetId}/items/${itemId}/memo`);
  }

  saveItemMemo(budgetId: string, itemId: string, lines: BudgetMemoLine[], result: number, notes?: string | null) {
    return this.http.put(`/budgets/${budgetId}/items/${itemId}/memo`, { lines, result, notes });
  }

  deleteStageEntry(budgetId: string, stageId: string) {
    return this.http.delete(`/budgets/${budgetId}/stages/${stageId}`);
  }

  deleteItemEntry(budgetId: string, itemId: string) {
    return this.http.delete(`/budgets/${budgetId}/items/${itemId}`);
  }

  updatePrices(budgetId: string, body: { type: string; percentage?: number; value?: number }) {
    return this.http.post(`/budgets/${budgetId}/price-adjustment`, body);
  }

  worksheetReportUrl(budgetId: string) {
    return `${environment.baseUrl}/budgets/${budgetId}/reports/worksheet.pdf`;
  }

  serviceAbcReportUrl(budgetId: string) {
    return `${environment.baseUrl}/budgets/${budgetId}/reports/abc-services.pdf`;
  }

  analyticalReportUrl(budgetId: string) {
    return `${environment.baseUrl}/budgets/${budgetId}/reports/analytical.pdf`;
  }
}
