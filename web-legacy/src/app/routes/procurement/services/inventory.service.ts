import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { InventoryItem } from '../models/procurement.model';

@Injectable({ providedIn: 'root' })
export class InventoryService {
  private readonly http = inject(HttpClient);

  private url(projectId: string) { return `/projects/${projectId}/inventory`; }

  listItems(projectId: string) {
    return this.http.get<InventoryItem[]>(`${this.url(projectId)}/items`);
  }

  listBelowMinimum(projectId: string) {
    return this.http.get<InventoryItem[]>(`${this.url(projectId)}/items/below-minimum`);
  }
}
