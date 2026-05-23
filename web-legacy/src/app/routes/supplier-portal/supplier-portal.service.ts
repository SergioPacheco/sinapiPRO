import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface PortalQuotation {
  quotationId: string;
  itemDescription: string;
  quantity: number;
  unit: string;
  deadline?: string;
  status: string;
  supplierName: string;
  alreadyResponded: boolean;
}

export interface PortalSubmitRequest {
  unitPrice: number;
  deliveryDays?: number;
  notes?: string;
}

export interface PortalResponseConfirmation {
  message: string;
  supplierName: string;
}

@Injectable({ providedIn: 'root' })
export class SupplierPortalService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/supplier-portal';

  getQuotation(token: string) {
    return this.http.get<PortalQuotation>(`${this.baseUrl}/quotation`, { params: { token } });
  }

  submitResponse(token: string, request: PortalSubmitRequest) {
    return this.http.post<PortalResponseConfirmation>(`${this.baseUrl}/quotation/respond`, request, { params: { token } });
  }
}
