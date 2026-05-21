import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { PageHeader } from '@shared';
import { ItemType } from '../item-autocomplete/item-autocomplete';

interface CompositionItem {
  id: string;
  itemType: ItemType;
  code: string;
  description: string;
  unit: string;
  coefficient: number;
  latestPrice: number | null;
}

interface CompositionDetail {
  id: string;
  sinapiCode: string;
  description: string;
  unit: string;
  groupName: string;
  origin: string;
  version: number;
  editable: boolean;
  items: CompositionItem[];
}

interface ItemGroup {
  type: ItemType;
  label: string;
  items: CompositionItem[];
  subtotal: number;
}

@Component({
  selector: 'app-composition-detail',
  standalone: true,
  imports: [DecimalPipe, MatButtonModule, MatCardModule, MatChipsModule, MatIconModule, MatTableModule, PageHeader],
  template: `
    <page-header />
    @if (composition()) {
      <mat-card>
        <mat-card-header>
          <mat-card-title>{{ composition()!.sinapiCode }} — {{ composition()!.description }}</mat-card-title>
          <mat-card-subtitle>
            {{ composition()!.unit }} | {{ composition()!.groupName }}
            <mat-chip-set>
              <mat-chip [highlighted]="composition()!.origin === 'PROPRIO'">{{ composition()!.origin }}</mat-chip>
            </mat-chip-set>
            @if (composition()!.version > 1) {
              <span class="version-badge">v{{ composition()!.version }}</span>
            }
          </mat-card-subtitle>
        </mat-card-header>
        <mat-card-actions>
          @if (composition()!.editable) {
            <button mat-button color="primary" (click)="edit()"><mat-icon>edit</mat-icon> Editar</button>
            <button mat-button color="warn" (click)="delete()"><mat-icon>delete</mat-icon> Excluir</button>
          }
          @if (composition()!.origin === 'SINAPI') {
            <button mat-flat-button color="accent" (click)="copyToOwn()" [disabled]="copying()">
              <mat-icon>content_copy</mat-icon> Copiar para Próprias
            </button>
          }
        </mat-card-actions>
        <mat-card-content>
          <h3>Insumos da Composição</h3>
          @if (!composition()!.items || composition()!.items.length === 0) {
            <p>Nenhum insumo vinculado.</p>
          } @else {
            @for (group of visibleGroups(); track group.type) {
              <div class="item-group">
                <div class="group-header">
                  <span class="group-title">{{ group.label }}</span>
                  <span class="group-subtotal">Subtotal: R$ {{ group.subtotal | number:'1.2-2' }}</span>
                </div>
                <table mat-table [dataSource]="group.items">
                  <ng-container matColumnDef="code">
                    <th mat-header-cell *matHeaderCellDef>Código</th>
                    <td mat-cell *matCellDef="let i">{{ i.code }}</td>
                  </ng-container>
                  <ng-container matColumnDef="description">
                    <th mat-header-cell *matHeaderCellDef>Descrição</th>
                    <td mat-cell *matCellDef="let i">{{ i.description }}</td>
                  </ng-container>
                  <ng-container matColumnDef="unit">
                    <th mat-header-cell *matHeaderCellDef>Unidade</th>
                    <td mat-cell *matCellDef="let i">{{ i.unit }}</td>
                  </ng-container>
                  <ng-container matColumnDef="coefficient">
                    <th mat-header-cell *matHeaderCellDef>Coeficiente</th>
                    <td mat-cell *matCellDef="let i">{{ i.coefficient | number:'1.2-6' }}</td>
                  </ng-container>
                  <ng-container matColumnDef="price">
                    <th mat-header-cell *matHeaderCellDef>Preço Unit.</th>
                    <td mat-cell *matCellDef="let i">{{ i.latestPrice != null ? (i.latestPrice | number:'1.2-2') : '—' }}</td>
                  </ng-container>
                  <ng-container matColumnDef="cost">
                    <th mat-header-cell *matHeaderCellDef>Custo</th>
                    <td mat-cell *matCellDef="let i">{{ i.latestPrice != null ? (i.coefficient * i.latestPrice | number:'1.2-2') : '—' }}</td>
                  </ng-container>
                  <tr mat-header-row *matHeaderRowDef="itemColumns"></tr>
                  <tr mat-row *matRowDef="let row; columns: itemColumns;"></tr>
                </table>
              </div>
            }

            <div class="total-cost">
              <strong>Custo Unitário Total: R$ {{ totalCost() | number:'1.2-2' }}</strong>
            </div>
          }
        </mat-card-content>
      </mat-card>
    }
  `,
  styles: `
    .version-badge { font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 12px; background: rgba(99,102,241,0.12); color: #6366f1; margin-left: 8px; }
    .item-group { margin-bottom: 24px; }
    .group-header { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 2px solid var(--mat-sys-outline-variant); margin-bottom: 4px; }
    .group-title { font-weight: 600; font-size: 14px; color: var(--mat-sys-on-surface); }
    .group-subtotal { font-size: 12px; color: var(--mat-sys-on-surface-variant); }
    .total-cost { margin-top: 16px; padding: 12px; background: var(--mat-sys-surface-container); border-radius: 8px; font-size: 16px; }
  `,
})
export class CompositionDetailComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  composition = signal<CompositionDetail | null>(null);
  copying = signal(false);

  itemColumns = ['code', 'description', 'unit', 'coefficient', 'price', 'cost'];

  private readonly typeLabels: Record<ItemType, string> = {
    MATERIAL: 'Materiais',
    LABOR: 'Mão de Obra',
    EQUIPMENT: 'Equipamentos',
    COMPOSITION: 'Composições Auxiliares',
  };

  groupedItems = computed(() => {
    const groups: Record<ItemType, CompositionItem[]> = {
      MATERIAL: [], LABOR: [], EQUIPMENT: [], COMPOSITION: []
    };
    for (const item of this.composition()?.items ?? []) {
      if (groups[item.itemType]) {
        groups[item.itemType].push(item);
      }
    }
    return groups;
  });

  visibleGroups = computed((): ItemGroup[] =>
    (['MATERIAL', 'LABOR', 'EQUIPMENT', 'COMPOSITION'] as ItemType[])
      .map(type => ({
        type,
        label: this.typeLabels[type],
        items: this.groupedItems()[type],
        subtotal: this.groupedItems()[type].reduce((sum, i) =>
          i.latestPrice != null ? sum + i.coefficient * i.latestPrice : sum, 0
        ),
      }))
      .filter(g => g.items.length > 0)
  );

  totalCost = computed(() =>
    (this.composition()?.items ?? []).reduce((sum, item) => {
      if (item.latestPrice == null) return sum;
      return sum + item.coefficient * item.latestPrice;
    }, 0)
  );

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    this.http.get<CompositionDetail>(`/api/v1/compositions/${id}`).subscribe(res => this.composition.set(res));
  }

  edit() { this.router.navigate(['edit'], { relativeTo: this.route }); }

  delete() {
    if (confirm('Excluir esta composição?')) {
      this.http.delete(`/api/v1/compositions/${this.composition()!.id}`)
        .subscribe(() => this.router.navigate(['/sinapi/compositions']));
    }
  }

  copyToOwn() {
    this.copying.set(true);
    this.http.post<{ id: string }>(`/api/v1/compositions/${this.composition()!.id}/copy`, {})
      .subscribe({
        next: (result) => {
          this.copying.set(false);
          this.router.navigate(['/sinapi/compositions', result.id, 'edit']);
        },
        error: () => this.copying.set(false),
      });
  }
}
