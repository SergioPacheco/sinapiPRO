import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Client, Employee, PaymentMethod, BankAccount, Team } from '../models/registry.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class RegistryService {
  private readonly http = inject(HttpClient);
  private readonly base = '/registry';

  listClients(page = 0, size = 20, search?: string) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<PageResponse<Client>>(`${this.base}/clients`, { params });
  }

  searchClients(term: string) {
    return this.http.get<PageResponse<Client>>(`${this.base}/clients`, {
      params: new HttpParams().set('search', term).set('size', 10),
    });
  }

  getClient(id: string) { return this.http.get<Client>(`${this.base}/clients/${id}`); }

  createClient(data: Partial<Client>) { return this.http.post<Client>(`${this.base}/clients`, data); }

  updateClient(id: string, data: Partial<Client>) { return this.http.put<Client>(`${this.base}/clients/${id}`, data); }

  deleteClient(id: string) { return this.http.delete<void>(`${this.base}/clients/${id}`); }

  listEmployees(type?: string, page = 0, size = 20) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (type) params = params.set('type', type);
    return this.http.get<PageResponse<Employee>>(`${this.base}/employees`, { params });
  }

  searchEmployees(term: string) {
    return this.http.get<PageResponse<Employee>>(`${this.base}/employees`, {
      params: new HttpParams().set('search', term).set('size', 10),
    });
  }

  getEmployee(id: string) { return this.http.get<Employee>(`${this.base}/employees/${id}`); }

  createEmployee(data: Partial<Employee>) { return this.http.post<Employee>(`${this.base}/employees`, data); }

  updateEmployee(id: string, data: Partial<Employee>) { return this.http.put<Employee>(`${this.base}/employees/${id}`, data); }

  deleteEmployee(id: string) { return this.http.delete<void>(`${this.base}/employees/${id}`); }

  listTeams(projectId?: string) {
    let params = new HttpParams();
    if (projectId) params = params.set('projectId', projectId);
    return this.http.get<Team[]>('/teams', { params });
  }

  getTeam(id: string) { return this.http.get<Team>(`/teams/${id}`); }

  createTeam(data: Partial<Team>) { return this.http.post<Team>('/teams', data); }

  updateTeam(id: string, data: Partial<Team>) { return this.http.put<Team>(`/teams/${id}`, data); }

  deleteTeam(id: string) { return this.http.delete<void>(`/teams/${id}`); }

  listPaymentMethods() { return this.http.get<PaymentMethod[]>(`${this.base}/payment-methods`); }

  createPaymentMethod(data: Partial<PaymentMethod>) { return this.http.post<PaymentMethod>(`${this.base}/payment-methods`, data); }

  listBankAccounts() { return this.http.get<BankAccount[]>(`${this.base}/bank-accounts`); }

  createBankAccount(data: Partial<BankAccount>) { return this.http.post<BankAccount>(`${this.base}/bank-accounts`, data); }

  deleteBankAccount(id: string) { return this.http.delete<void>(`${this.base}/bank-accounts/${id}`); }

  deletePaymentMethod(id: string) { return this.http.delete<void>(`${this.base}/payment-methods/${id}`); }

  // Generic CRUD for simple registries
  listGeneric<T = any>(path: string) { return this.http.get<T[]>(path); }
  createGeneric<T = any>(path: string, data: any) { return this.http.post<T>(path, data); }
  updateGeneric<T = any>(path: string, id: string, data: any) { return this.http.put<T>(`${path}/${id}`, data); }
  deleteGeneric(path: string, id: string) { return this.http.delete<void>(`${path}/${id}`); }
}
