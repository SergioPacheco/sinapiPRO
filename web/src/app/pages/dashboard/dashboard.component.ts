import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DecimalPipe, RouterLink],
  template: `
    <h2 style="margin:0 0 1rem;color:var(--sp-text)">Dashboard</h2>

    <!-- KPIs -->
    <div class="kpi-grid">
      <div class="kpi-card"><span class="kpi-label">Obras Ativas</span><strong class="kpi-value">{{ portfolio().activeProjects }}</strong></div>
      <div class="kpi-card"><span class="kpi-label">Orçamentos</span><strong class="kpi-value">{{ portfolio().totalBudgets }}</strong></div>
      <div class="kpi-card"><span class="kpi-label">Valor Orçado</span><strong class="kpi-value">{{ portfolio().totalBudgetValue | number:'1.0-0' }}</strong></div>
      <div class="kpi-card"><span class="kpi-label">Medições Pendentes</span><strong class="kpi-value warn">{{ pendingMeasurements().length }}</strong></div>
      <div class="kpi-card"><span class="kpi-label">Contratos a Vencer</span><strong class="kpi-value warn">{{ expiringContracts().length }}</strong></div>
      <div class="kpi-card"><span class="kpi-label">Projetos em Risco</span><strong class="kpi-value danger">{{ projectsAtRisk().length }}</strong></div>
    </div>

    <!-- Medições Pendentes -->
    @if (pendingMeasurements().length > 0) {
      <div class="section">
        <h3>Medições Aguardando Aprovação</h3>
        <table class="mini-table">
          <tr><th>Obra</th><th>Medição</th><th>Valor</th></tr>
          @for (m of pendingMeasurements(); track $index) {
            <tr><td>{{ m.projectName }}</td><td>#{{ m.number }}</td><td class="text-right font-mono">{{ m.measuredValue | number:'1.2-2' }}</td></tr>
          }
        </table>
      </div>
    }

    <!-- Contratos a Vencer -->
    @if (expiringContracts().length > 0) {
      <div class="section">
        <h3>Contratos Próximos do Vencimento</h3>
        <table class="mini-table">
          <tr><th>Contrato</th><th>Fornecedor</th><th>Vencimento</th></tr>
          @for (c of expiringContracts(); track $index) {
            <tr><td>{{ c.contractNumber }}</td><td>{{ c.supplierName }}</td><td>{{ c.endDate }}</td></tr>
          }
        </table>
      </div>
    }
  `,
  styles: [`
    .kpi-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 12px; margin-bottom: 24px; }
    .kpi-card { background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: 8px; padding: 14px; }
    .kpi-label { display: block; font-size: 10px; color: var(--sp-text-muted); text-transform: uppercase; letter-spacing: 0.3px; margin-bottom: 4px; }
    .kpi-value { font-size: 22px; color: var(--sp-text); }
    .kpi-value.warn { color: #f59e0b; }
    .kpi-value.danger { color: #ef4444; }
    .section { margin-bottom: 20px; }
    .section h3 { font-size: 13px; color: var(--sp-text-muted); margin: 0 0 8px; }
    .mini-table { width: 100%; border-collapse: collapse; font-size: 12px; background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: 6px; overflow: hidden; }
    .mini-table th { background: var(--sp-surface-hover); padding: 6px 10px; text-align: left; font-size: 10px; color: var(--sp-text-muted); text-transform: uppercase; }
    .mini-table td { padding: 6px 10px; border-top: 1px solid var(--sp-border); color: var(--sp-text); }
  `],
})
export class DashboardComponent implements OnInit {
  private http = inject(HttpClient);

  portfolio = signal<any>({ activeProjects: 0, totalBudgets: 0, totalBudgetValue: 0 });
  pendingMeasurements = signal<any[]>([]);
  expiringContracts = signal<any[]>([]);
  projectsAtRisk = signal<any[]>([]);

  ngOnInit() {
    this.http.get<any>('/analytics/portfolio').subscribe({ next: r => this.portfolio.set(r || {}), error: () => {} });
    this.http.get<any>('/analytics/pending-measurements').subscribe({ next: r => this.pendingMeasurements.set(r || []), error: () => {} });
    this.http.get<any>('/analytics/contracts-expiring').subscribe({ next: r => this.expiringContracts.set(r || []), error: () => {} });
    this.http.get<any>('/analytics/projects-at-risk').subscribe({ next: r => this.projectsAtRisk.set(r || []), error: () => {} });
  }
}
