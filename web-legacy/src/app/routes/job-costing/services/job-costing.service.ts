import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { CostCode } from '../models/job-costing.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class JobCostingService {
  private readonly http = inject(HttpClient);

  private url(projectId: string) { return `/projects/${projectId}/cost-codes`; }

  listCostCodes(projectId: string, page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<CostCode>>(this.url(projectId), { params });
  }

  getWipReport(projectId: string) {
    return this.http.get<any>(`/projects/${projectId}/wip-report`);
  }
}
