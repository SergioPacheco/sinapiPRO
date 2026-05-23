import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { PageHeader } from '@shared';
import { ItemAutocompleteComponent, ItemSearchResult, ItemType } from '../item-autocomplete/item-autocomplete';

interface CompositionItemForm {
  code: string;
  description: string;
  unit: string;
  coefficient: number;
  itemType: ItemType;
  materialCode: string;
  childCompositionId: string | null;
  price: number | null;
}

@Component({
  selector: 'app-composition-form',
  standalone: true,
  imports: [FormsModule, DecimalPipe, MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, MatIconModule, MatSelectModule, PageHeader, ItemAutocompleteComponent],
  template: `
    <page-header [title]="isEdit ? 'Editar Composição Própria' : 'Nova Composição Própria'" />
    <mat-card>
      <mat-card-content>
        <div class="form-grid">
          @if (!isEdit) {
            <mat-form-field><mat-label>Código</mat-label><input matInput [(ngModel)]="form.code" required /></mat-form-field>
          }
          <mat-form-field><mat-label>Descrição</mat-label><input matInput [(ngModel)]="form.description" required /></mat-form-field>
          <mat-form-field><mat-label>Unidade</mat-label><input matInput [(ngModel)]="form.unit" required /></mat-form-field>
          <mat-form-field><mat-label>Grupo</mat-label><input matInput [(ngModel)]="form.groupName" /></mat-form-field>
        </div>

        <h3>Insumos</h3>
        <app-item-autocomplete (itemSelected)="addItemFromSearch($event)" />

        @for (item of items(); track $index) {
          <div class="item-row">
            <span class="item-type-badge" [attr.data-type]="item.itemType">{{ typeLabel(item.itemType) }}</span>
            <span class="item-desc">{{ item.code }} — {{ item.description }}</span>
            <mat-form-field class="coef-field">
              <mat-label>Coef.</mat-label>
              <input matInput type="number" [(ngModel)]="item.coefficient" (ngModelChange)="recalculate()" />
            </mat-form-field>
            <span class="item-price">{{ item.price != null ? (item.price | number:'1.2-2') : '—' }}</span>
            <span class="item-cost">{{ item.price != null ? (item.coefficient * item.price | number:'1.2-2') : '—' }}</span>
            <button mat-icon-button color="warn" (click)="removeItem($index)"><mat-icon>remove_circle</mat-icon></button>
          </div>
        }

        @if (itemsWithoutPrice() > 0) {
          <p class="price-warning">⚠ {{ itemsWithoutPrice() }} item(ns) sem preço disponível — excluídos do cálculo.</p>
        }

        <div class="cost-summary">
          <div class="subtotals">
            @for (entry of subtotalEntries(); track entry.type) {
              <span>{{ typeLabel(entry.type) }}: R$ {{ entry.value | number:'1.2-2' }}</span>
            }
          </div>
          <div class="total">
            <strong>Custo Unitário Total: R$ {{ totalCost() | number:'1.2-2' }}</strong>
          </div>
        </div>
      </mat-card-content>
      <mat-card-actions>
        <button mat-flat-button color="primary" (click)="save()" [disabled]="!form.description || !form.unit">Salvar</button>
        <button mat-button (click)="cancel()">Cancelar</button>
      </mat-card-actions>
    </mat-card>
  `,
  styles: `
    .form-grid { display: flex; flex-direction: column; gap: 8px; max-width: 600px; }
    .item-row { display: flex; gap: 8px; align-items: center; padding: 8px 0; border-bottom: 1px solid var(--mat-sys-outline-variant); }
    .item-type-badge { font-size: 10px; font-weight: 600; padding: 2px 6px; border-radius: 4px; text-transform: uppercase;
      &[data-type="MATERIAL"] { background: rgba(59,130,246,0.12); color: #3b82f6; }
      &[data-type="LABOR"] { background: rgba(245,158,11,0.12); color: #f59e0b; }
      &[data-type="EQUIPMENT"] { background: rgba(107,114,128,0.12); color: #6b7280; }
      &[data-type="COMPOSITION"] { background: rgba(16,185,129,0.12); color: #10b981; }
    }
    .item-desc { flex: 1; font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .coef-field { width: 80px; }
    .item-price, .item-cost { width: 80px; text-align: right; font-size: 12px; color: var(--mat-sys-on-surface-variant); }
    .price-warning { font-size: 12px; color: #f59e0b; margin: 8px 0; }
    .cost-summary { margin-top: 16px; padding: 12px; background: var(--mat-sys-surface-container); border-radius: 8px; }
    .subtotals { display: flex; gap: 16px; font-size: 12px; color: var(--mat-sys-on-surface-variant); margin-bottom: 8px; }
    .total { font-size: 16px; color: var(--mat-sys-on-surface); }
  `,
})
export class CompositionFormComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  isEdit = false;
  compId = '';
  form = { code: '', description: '', unit: '', groupName: '' };

  items = signal<CompositionItemForm[]>([]);

  totalCost = computed(() =>
    this.items().reduce((sum, item) => {
      if (item.price == null) return sum;
      return sum + item.coefficient * item.price;
    }, 0)
  );

  subtotalsByType = computed(() => {
    const groups = new Map<ItemType, number>();
    for (const item of this.items()) {
      if (item.price == null) continue;
      const current = groups.get(item.itemType) ?? 0;
      groups.set(item.itemType, current + item.coefficient * item.price);
    }
    return groups;
  });

  subtotalEntries = computed(() =>
    Array.from(this.subtotalsByType().entries())
      .filter(([_, v]) => v > 0)
      .map(([type, value]) => ({ type, value }))
  );

  itemsWithoutPrice = computed(() => this.items().filter(i => i.price == null).length);

  typeLabel(type: ItemType): string {
    const labels: Record<ItemType, string> = { MATERIAL: 'Material', LABOR: 'Mão de Obra', EQUIPMENT: 'Equipamento', COMPOSITION: 'Composição' };
    return labels[type] || type;
  }

  ngOnInit() {
    this.compId = this.route.snapshot.paramMap.get('id') || '';
    this.isEdit = !!this.compId;
    if (this.isEdit) {
      this.http.get<any>(`/api/v1/compositions/${this.compId}`).subscribe(c => {
        this.form = { code: c.sinapiCode, description: c.description, unit: c.unit, groupName: c.groupName || '' };
        this.items.set((c.items || []).map((i: any) => ({
          code: i.code, description: i.description, unit: i.unit,
          coefficient: i.coefficient, itemType: i.itemType,
          materialCode: i.itemType !== 'COMPOSITION' ? i.code : '',
          childCompositionId: i.itemType === 'COMPOSITION' ? i.id : null,
          price: i.latestPrice || null,
        })));
      });
    }
  }

  addItemFromSearch(result: ItemSearchResult) {
    this.items.update(items => [...items, {
      code: result.code,
      description: result.description,
      unit: result.unit,
      coefficient: 1,
      itemType: result.type,
      materialCode: result.type !== 'COMPOSITION' ? result.code : '',
      childCompositionId: result.type === 'COMPOSITION' ? result.id : null,
      price: result.latestPrice,
    }]);
  }

  removeItem(index: number) {
    this.items.update(items => items.filter((_, i) => i !== index));
  }

  recalculate() {
    // Trigger signal update by creating new array reference
    this.items.update(items => [...items]);
  }

  save() {
    const validItems = this.items().map(i => ({
      materialCode: i.materialCode || null,
      childCompositionId: i.childCompositionId || null,
      coefficient: i.coefficient,
      itemType: i.itemType,
    }));
    const payload = { ...this.form, items: validItems };
    if (this.isEdit) {
      this.http.put(`/api/v1/compositions/${this.compId}`, payload)
          .subscribe(() => this.router.navigate(['/sinapi/compositions', this.compId]));
    } else {
      this.http.post<any>('/api/v1/compositions', payload)
          .subscribe(c => this.router.navigate(['/sinapi/compositions', c.id]));
    }
  }

  cancel() { this.router.navigate(['/sinapi/compositions']); }
}
