import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { Composition, Material, CompositionCostResult } from '../models/sinapi.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class SinapiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.baseUrl;

  listCompositions(page = 0, size = 20, search?: string) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<PageResponse<Composition>>(`${this.baseUrl}/sinapi/compositions`, { params });
  }

  getCompositionCost(id: string, state: string, referenceMonth: string) {
    const params = new HttpParams().set('state', state).set('referenceMonth', referenceMonth);
    return this.http.get<CompositionCostResult>(`${this.baseUrl}/sinapi/compositions/${id}/cost`, { params });
  }

  listMaterials(page = 0, size = 20, search?: string) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<PageResponse<Material>>(`${this.baseUrl}/sinapi/materials`, { params });
  }
}
