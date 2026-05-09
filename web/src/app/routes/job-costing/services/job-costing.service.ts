import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { CostCode } from '../models/job-costing.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class JobCostingService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.baseUrl}/job-costing`;

  listCostCodes(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<CostCode>>(`${this.baseUrl}/cost-codes`, { params });
  }

  getWipReport() {
    return this.http.get<any>(`${this.baseUrl}/wip-report`);
  }
}
