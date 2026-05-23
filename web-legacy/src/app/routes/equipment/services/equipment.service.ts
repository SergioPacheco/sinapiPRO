import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@env/environment';
import { Equipment } from '../models/equipment.model';
import { PageResponse } from '../../budget/services/budget.service';

@Injectable({ providedIn: 'root' })
export class EquipmentService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.baseUrl}/equipment`;

  list(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Equipment>>(this.baseUrl, { params });
  }

  getById(id: string) {
    return this.http.get<Equipment>(`${this.baseUrl}/${id}`);
  }
}
