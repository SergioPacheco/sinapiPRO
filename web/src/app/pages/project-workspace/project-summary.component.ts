import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CardModule } from 'primeng/card';

@Component({
  selector: 'app-project-summary',
  standalone: true,
  imports: [CardModule],
  template: `
    @if (data(); as d) {
      <div class="grid">
        <div class="col-12 md:col-4">
          <p-card header="Planejamento">
            <div class="stat-row"><span>Orçamento</span><strong>{{ d.planning?.hasBudget ? '✓' : '—' }}</strong></div>
            <div class="stat-row"><span>Contrato</span><strong>{{ d.planning?.hasContract ? '✓' : '—' }}</strong></div>
            <div class="stat-row"><span>Cronograma</span><strong>{{ d.planning?.hasSchedule ? '✓' : '—' }}</strong></div>
          </p-card>
        </div>
        <div class="col-12 md:col-4">
          <p-card header="Execução">
            <div class="stat-row"><span>Medições pendentes</span><strong>{{ d.execution?.pendingMeasurements || 0 }}</strong></div>
            <div class="stat-row"><span>Pedidos pendentes</span><strong>{{ d.execution?.pendingOrders || 0 }}</strong></div>
            <div class="stat-row"><span>Progresso</span><strong>{{ d.execution?.progressPct || 0 }}%</strong></div>
          </p-card>
        </div>
        <div class="col-12 md:col-4">
          <p-card header="Financeiro">
            <div class="stat-row"><span>A pagar</span><strong class="currency">{{ formatCurrency(d.financial?.totalPayable) }}</strong></div>
            <div class="stat-row"><span>A receber</span><strong class="currency">{{ formatCurrency(d.financial?.totalReceivable) }}</strong></div>
          </p-card>
        </div>
      </div>
    }
  `,
  styles: [`.stat-row { display: flex; justify-content: space-between; padding: 0.5rem 0; border-bottom: 1px solid var(--sp-border); font-size: 13px; }`],
})
export class ProjectSummaryComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  data = signal<any>(null);

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get(`/projects/${id}/dashboard`).subscribe(d => this.data.set(d));
  }

  formatCurrency(v: number) {
    if (!v) return 'R$ 0';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL', maximumFractionDigits: 0 }).format(v);
  }
}
