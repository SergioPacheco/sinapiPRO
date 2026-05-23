import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Composition, Material, CompositionCostResult } from '../models/sinapi.model';
import { PageResponse } from '../../budget/services/budget.service';

export interface MaterialFilters { q?: string; origin?: string; unit?: string; state?: string; referenceMonth?: string; }
export interface CompositionFilters { q?: string; origin?: string; unit?: string; groupName?: string; }
export interface FilterOptions { units: string[]; origins: string[]; groups?: string[]; }

@Injectable({ providedIn: 'root' })
export class SinapiService {
  private readonly http = inject(HttpClient);

  listMaterials(page = 0, size = 20, filters: MaterialFilters = {}) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filters.q) params = params.set('q', filters.q);
    if (filters.origin) params = params.set('origin', filters.origin);
    if (filters.unit) params = params.set('unit', filters.unit);
    if (filters.state) params = params.set('state', filters.state);
    if (filters.referenceMonth) params = params.set('referenceMonth', filters.referenceMonth);
    return this.http.get<PageResponse<Material>>('/materials', { params });
  }

  getMaterialFilters() {
    return this.http.get<FilterOptions>('/materials/filters');
  }

  listCompositions(page = 0, size = 20, filters: CompositionFilters = {}) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filters.q) params = params.set('q', filters.q);
    if (filters.origin) params = params.set('origin', filters.origin);
    if (filters.unit) params = params.set('unit', filters.unit);
    if (filters.groupName) params = params.set('groupName', filters.groupName);
    return this.http.get<PageResponse<Composition>>('/compositions', { params });
  }

  getCompositionFilters() {
    return this.http.get<FilterOptions>('/compositions/filters');
  }

  getCompositionCost(id: string, state: string, month: string) {
    const params = new HttpParams().set('state', state).set('month', month);
    return this.http.get<CompositionCostResult>(`/compositions/${id}/cost`, { params });
  }
}
