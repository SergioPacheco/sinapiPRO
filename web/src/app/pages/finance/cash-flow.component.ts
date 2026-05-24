import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-cash-flow',
  standalone: true,
  imports: [DecimalPipe, TableModule, ButtonModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Fluxo de Caixa</h2>
    </div>

    <!-- Resumo -->
    <div class="flex gap-3 mb-3" style="font-size:12px">
      <div class="metric-card"><span class="metric-label">Receitas</span><strong style="color:#10b981">{{ totals().receitas | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">Despesas</span><strong style="color:#ef4444">{{ totals().despesas | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">Saldo</span><strong [style.color]="totals().saldo >= 0 ? '#10b981' : '#ef4444'">{{ totals().saldo | number:'1.2-2' }}</strong></div>
    </div>

    <p-table [value]="cashFlow()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
      <ng-template pTemplate="header">
        <tr>
          <th>Período</th>
          <th class="text-right" style="width:120px">Receitas</th>
          <th class="text-right" style="width:120px">Despesas</th>
          <th class="text-right" style="width:120px">Saldo</th>
          <th class="text-right" style="width:120px">Acumulado</th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-row>
        <tr>
          <td>{{ row.period }}</td>
          <td class="text-right font-mono" style="color:#10b981">{{ row.income | number:'1.2-2' }}</td>
          <td class="text-right font-mono" style="color:#ef4444">{{ row.expense | number:'1.2-2' }}</td>
          <td class="text-right font-mono" [style.color]="row.balance >= 0 ? '#10b981' : '#ef4444'">{{ row.balance | number:'1.2-2' }}</td>
          <td class="text-right font-mono" style="font-weight:600">{{ row.accumulated | number:'1.2-2' }}</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Sem dados de fluxo de caixa</td></tr></ng-template>
    </p-table>
  `,
  styles: [`.metric-card { background:var(--sp-surface-card); border:1px solid var(--sp-border); border-radius:6px; padding:10px 14px; } .metric-label { display:block; font-size:10px; color:var(--sp-text-muted); text-transform:uppercase; margin-bottom:2px; }`],
})
export class CashFlowComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);

  cashFlow = signal<any[]>([]);
  totals = signal<any>({ receitas: 0, despesas: 0, saldo: 0 });

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/analytics/projects/${this.pid}/cash-flow`).subscribe({
      next: r => {
        const data = r?.periods || r || [];
        this.cashFlow.set(data);
        const receitas = data.reduce((s: number, d: any) => s + (d.income || 0), 0);
        const despesas = data.reduce((s: number, d: any) => s + (d.expense || 0), 0);
        this.totals.set({ receitas, despesas, saldo: receitas - despesas });
      },
      error: () => {},
    });
  }
}
