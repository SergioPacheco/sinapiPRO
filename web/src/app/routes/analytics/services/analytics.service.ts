import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface EvmData {
  pv: number;
  ev: number;
  ac: number;
  cpi: number;
  spi: number;
}

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private readonly http = inject(HttpClient);

  getPortfolio() {
    return this.http.get<any>('/analytics/portfolio');
  }

  getEvm(projectId: string) {
    return this.http.get<EvmData>(`/analytics/projects/${projectId}/earned-value`);
  }

  getCashFlow(projectId: string) {
    return this.http.get<any>(`/analytics/projects/${projectId}/cash-flow`);
  }
}
