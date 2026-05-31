interface BudgetStage { id: string; name: string; code: string; totalDirectCost: number; totalWithBdi: number; itemCount: number; items: BudgetItem[]; }
interface BudgetItem { id: string; compositionCode: string; description: string; unit: string; quantity: number; unitCost: number; bdiPct: number; totalCost: number; totalWithBdi: number; }
interface Budget { id: string; code: string; title: string; status: string; totalDirectCost: number; totalWithBdi: number; itemCount: number; createdAt: string; }

import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe } from '@angular/common';
import { AddItemDialogComponent } from './add-item-dialog.component';
import { AbcCurveComponent } from './abc-curve.component';

@Component({
  selector: 'app-budget-detail',
  standalone: true,
  imports: [DecimalPipe, AddItemDialogComponent, AbcCurveComponent],
  template: `
    @if (budget(); as b) {
      <div class="budget-detail">
        <header class="detail-header">
          <div>
            <h2>{{ b.code }} — {{ b.title }}</h2>
            <span class="badge" [attr.data-status]="b.status">{{ b.status }}</span>
          </div>
          <div class="header-actions">
            <button class="btn-outline" (click)="exportExcel()">📥 Exportar Excel</button>
            <button class="btn-primary" (click)="showAddItem = true">+ Adicionar Item</button>
          </div>
        </header>

        <div class="summary-cards">
          <div class="card"><span class="label">Custo Direto</span><span class="value">R$ {{ b.totalDirectCost | number:'1.2-2' }}</span></div>
          <div class="card"><span class="label">Com BDI</span><span class="value">R$ {{ b.totalWithBdi | number:'1.2-2' }}</span></div>
          <div class="card"><span class="label">Itens</span><span class="value">{{ b.itemCount }}</span></div>
          <div class="card"><span class="label">Etapas</span><span class="value">{{ stages().length }}</span></div>
        </div>

        @for (stage of stages(); track stage.id) {
          <section class="stage-section">
            <h3>{{ stage.code }} — {{ stage.name }} <span class="stage-total">R$ {{ stage.totalWithBdi | number:'1.2-2' }}</span></h3>
            <table>
              <thead>
                <tr><th>Código</th><th>Descrição</th><th>Un</th><th>Qtd</th><th>Custo Unit.</th><th>BDI%</th><th>Total</th></tr>
              </thead>
              <tbody>
                @for (item of stage.items; track item.id) {
                  <tr>
                    <td class="code">{{ item.compositionCode }}</td>
                    <td>{{ item.description }}</td>
                    <td>{{ item.unit }}</td>
                    <td class="num">{{ item.quantity | number:'1.2-2' }}</td>
                    <td class="num">{{ item.unitCost | number:'1.2-2' }}</td>
                    <td class="num">{{ item.bdiPct | number:'1.1-1' }}%</td>
                    <td class="num total">{{ item.totalWithBdi | number:'1.2-2' }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </section>
        }
      </div>
    }

    <section class="abc-section">
      <app-abc-curve [items]="allItems()" />
    </section>

    @if (showAddItem) {
      <app-add-item-dialog [budgetId]="budgetId" (close)="showAddItem = false" (added)="reload()" />
    }
  `,
  styles: [`
    .detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;
      h2 { margin: 0; color: #e0e0e0; display: inline; margin-right: 0.75rem; }
      .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; background: #0f3460; color: #4fc3f7; }
    }
    .header-actions { display: flex; gap: 0.75rem; }
    .btn-primary { padding: 0.5rem 1rem; border-radius: 6px; border: none; background: #4fc3f7; color: #1a1a2e; font-weight: 600; cursor: pointer; }
    .btn-outline { padding: 0.5rem 1rem; border-radius: 6px; border: 1px solid #4a4a6a; background: transparent; color: #b0b0b0; cursor: pointer; &:hover { background: #2a2a4a; } }
    .summary-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-bottom: 2rem; }
    .card { background: #16213e; border-radius: 10px; padding: 1.25rem; border: 1px solid #2a2a4a;
      display: flex; flex-direction: column; gap: 0.25rem;
      .label { font-size: 0.75rem; color: #8a8aaa; text-transform: uppercase; }
      .value { font-size: 1.4rem; font-weight: 700; }
    }
    .stage-section { margin-bottom: 2rem;
      h3 { color: #b0b0b0; font-size: 0.9rem; margin-bottom: 0.75rem; .stage-total { float: right; color: #4fc3f7; } }
    }
    table { width: 100%; border-collapse: collapse; background: #16213e; border-radius: 8px; overflow: hidden; }
    th { text-align: left; padding: 0.6rem 0.75rem; color: #8a8aaa; font-size: 0.75rem; border-bottom: 1px solid #2a2a4a; }
    td { padding: 0.6rem 0.75rem; border-bottom: 1px solid #1a1a2e; font-size: 0.85rem; }
    .code { color: #4fc3f7; font-family: monospace; }
    .num { text-align: right; font-family: monospace; }
    .total { font-weight: 600; color: #e0e0e0; }
  `]
})
export class BudgetDetailComponent implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);

  budgetId = '';
  budget = signal<Budget | null>(null);
  stages = signal<BudgetStage[]>([]);
  allItems = computed(() => this.stages().flatMap(s => s.items));
  showAddItem = false;

  ngOnInit() {
    this.budgetId = this.route.snapshot.params['id'];
    this.reload();
  }

  reload() {
    this.showAddItem = false;
    this.http.get<Budget>(`/budgets/${this.budgetId}`).subscribe(b => this.budget.set(b));
    this.http.get<BudgetStage[]>(`/budgets/${this.budgetId}/stages`).subscribe(s => this.stages.set(s));
  }

  exportExcel() {
    this.http.get(`/budgets/${this.budgetId}/export/excel`, { responseType: 'blob' }).subscribe(blob => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `orcamento-${this.budget()?.code}.xlsx`;
      a.click();
      URL.revokeObjectURL(url);
    });
  }
}
