import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';

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
  private readonly baseUrl = `${environment.baseUrl}/analytics`;

  getEvm(budgetId: string) {
    return this.http.get<EvmData>(`${this.baseUrl}/evm/${budgetId}`);
  }
}
