import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Client, Employee, PaymentMethod, BankAccount } from '../models/registry.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class RegistryService {
  private readonly http = inject(HttpClient);
  private readonly base = '/registry';

  listClients(page = 0, size = 20) {
    return this.http.get<PageResponse<Client>>(`${this.base}/clients`, { params: new HttpParams().set('page', page).set('size', size) });
  }

  createClient(data: Partial<Client>) { return this.http.post<Client>(`${this.base}/clients`, data); }

  listEmployees(type?: string, page = 0, size = 20) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (type) params = params.set('type', type);
    return this.http.get<PageResponse<Employee>>(`${this.base}/employees`, { params });
  }

  createEmployee(data: Partial<Employee>) { return this.http.post<Employee>(`${this.base}/employees`, data); }

  listPaymentMethods() { return this.http.get<PaymentMethod[]>(`${this.base}/payment-methods`); }

  createPaymentMethod(data: Partial<PaymentMethod>) { return this.http.post<PaymentMethod>(`${this.base}/payment-methods`, data); }

  listBankAccounts() { return this.http.get<BankAccount[]>(`${this.base}/bank-accounts`); }

  createBankAccount(data: Partial<BankAccount>) { return this.http.post<BankAccount>(`${this.base}/bank-accounts`, data); }
}
