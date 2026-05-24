import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { CurrencyDisplayComponent } from '../../shared/components';

@Component({
  selector: 'app-project-summary',
  standalone: true,
  imports: [CardModule, ButtonModule, CurrencyDisplayComponent],
  template: `
    @if (data(); as d) {
      <!-- PROCESS MAP -->
      <div class="process-map">
        <div class="process-title">Fluxo da Obra</div>
        <div class="process-flow">
          @for (step of processSteps(d); track step.id) {
            <div class="process-step" [class]="step.status" (click)="navigate(step.route)">
              <div class="step-icon"><i [class]="'pi pi-' + step.icon"></i></div>
              <div class="step-label">{{ step.label }}</div>
              <div class="step-badge">
                @if (step.status === 'done') { <i class="pi pi-check-circle"></i> }
                @else if (step.status === 'active') { <span class="pulse"></span> {{ step.info }} }
                @else if (step.status === 'warning') { <i class="pi pi-exclamation-triangle"></i> {{ step.info }} }
                @else { <i class="pi pi-circle"></i> }
              </div>
            </div>
            @if (!$last) { <div class="process-arrow"><i class="pi pi-arrow-right"></i></div> }
          }
        </div>
      </div>

      <!-- KPIs -->
      <div class="grid mt-3">
        <div class="col-6 md:col-3"><div class="kpi"><div class="kpi-value">{{ d.execution?.dailyLogs || 0 }}</div><div class="kpi-label">Diários</div></div></div>
        <div class="col-6 md:col-3"><div class="kpi"><div class="kpi-value">{{ d.execution?.measurements || 0 }}</div><div class="kpi-label">Medições</div></div></div>
        <div class="col-6 md:col-3"><div class="kpi"><div class="kpi-value">{{ d.execution?.purchaseOrders || 0 }}</div><div class="kpi-label">Pedidos</div></div></div>
        <div class="col-6 md:col-3"><div class="kpi"><div class="kpi-value"><sp-currency [value]="d.financial?.totalPayable || 0" /></div><div class="kpi-label">A Pagar</div></div></div>
      </div>

      <!-- NEXT ACTIONS -->
      @if (d.nextActions?.length) {
        <div class="next-actions mt-3">
          <div class="section-title">Próximas Ações</div>
          <div class="actions-grid">
            @for (action of d.nextActions; track action.id) {
              <div class="action-card" (click)="navigate(action.route)">
                <i [class]="'pi pi-' + action.icon" class="action-icon"></i>
                <span class="action-label">{{ action.label }}</span>
                <i class="pi pi-chevron-right action-arrow"></i>
              </div>
            }
          </div>
        </div>
      }

      <!-- CARDS DETALHADOS -->
      <div class="grid mt-3">
        <div class="col-12 md:col-4">
          <p-card header="Planejamento">
            <div class="stat-row"><span>Orçamento</span><strong>{{ d.planning?.hasBudget ? '✓ Efetivado' : '— Pendente' }}</strong></div>
            <div class="stat-row"><span>Contrato</span><strong>{{ d.planning?.hasContract ? '✓ Assinado' : '— Pendente' }}</strong></div>
            <div class="stat-row"><span>Cronograma</span><strong>{{ d.planning?.hasSchedule ? '✓ Definido' : '— Pendente' }}</strong></div>
            <div class="stat-row"><span>Equipe</span><strong>{{ d.planning?.hasTeam ? '✓ Montada' : '— Pendente' }}</strong></div>
          </p-card>
        </div>
        <div class="col-12 md:col-4">
          <p-card header="Execução">
            <div class="stat-row"><span>Medições pendentes</span><strong class="text-orange-500">{{ d.execution?.pendingMeasurements || 0 }}</strong></div>
            <div class="stat-row"><span>Pedidos pendentes</span><strong class="text-orange-500">{{ d.execution?.pendingOrders || 0 }}</strong></div>
            <div class="stat-row"><span>Diários de obra</span><strong>{{ d.execution?.dailyLogs || 0 }}</strong></div>
          </p-card>
        </div>
        <div class="col-12 md:col-4">
          <p-card header="Financeiro">
            <div class="stat-row"><span>A pagar</span><strong class="text-red-400">{{ formatCurrency(d.financial?.totalPayable) }}</strong></div>
            <div class="stat-row"><span>A receber</span><strong class="text-green-400">{{ formatCurrency(d.financial?.totalReceivable) }}</strong></div>
            <div class="stat-row"><span>Saldo</span><strong>{{ formatCurrency((d.financial?.totalReceivable || 0) - (d.financial?.totalPayable || 0)) }}</strong></div>
          </p-card>
        </div>
      </div>
    }
  `,
  styles: [`
    .process-map { background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: var(--sp-radius); padding: 1.25rem; }
    .process-title { font-weight: 700; font-size: 14px; margin-bottom: 1rem; color: var(--sp-text-muted); text-transform: uppercase; letter-spacing: 0.5px; }
    .process-flow { display: flex; align-items: center; gap: 4px; overflow-x: auto; padding: 0.5rem 0; }
    .process-step { display: flex; flex-direction: column; align-items: center; gap: 6px; padding: 0.75rem 1rem; border-radius: 8px; cursor: pointer; min-width: 90px; transition: all 0.15s; }
    .process-step:hover { background: var(--sp-surface-hover); transform: translateY(-2px); }
    .process-step.done .step-icon { background: var(--sp-success); color: white; }
    .process-step.active .step-icon { background: var(--sp-primary); color: white; animation: pulse-ring 1.5s infinite; }
    .process-step.warning .step-icon { background: var(--sp-warning); color: white; }
    .process-step.pending .step-icon { background: var(--sp-surface-ground); color: var(--sp-text-muted); }
    .step-icon { width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.1rem; }
    .step-label { font-size: 11px; font-weight: 600; text-align: center; }
    .step-badge { font-size: 10px; color: var(--sp-text-muted); display: flex; align-items: center; gap: 3px; }
    .step-badge .pi-check-circle { color: var(--sp-success); }
    .step-badge .pi-exclamation-triangle { color: var(--sp-warning); }
    .process-arrow { color: var(--sp-text-muted); font-size: 0.8rem; }
    @keyframes pulse-ring { 0% { box-shadow: 0 0 0 0 rgba(var(--sp-primary-rgb, 99,102,241), 0.4); } 70% { box-shadow: 0 0 0 8px transparent; } 100% { box-shadow: 0 0 0 0 transparent; } }

    .kpi { text-align: center; padding: 1rem; background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: var(--sp-radius); }
    .kpi-value { font-size: 1.5rem; font-weight: 700; } .kpi-label { font-size: 11px; color: var(--sp-text-muted); text-transform: uppercase; margin-top: 4px; }

    .section-title { font-weight: 700; font-size: 13px; color: var(--sp-text-muted); text-transform: uppercase; margin-bottom: 0.75rem; }
    .actions-grid { display: flex; flex-direction: column; gap: 6px; }
    .action-card { display: flex; align-items: center; gap: 12px; padding: 0.75rem 1rem; background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: 6px; cursor: pointer; transition: all 0.15s; }
    .action-card:hover { border-color: var(--sp-primary); background: color-mix(in srgb, var(--sp-primary) 5%, transparent); }
    .action-icon { color: var(--sp-primary); font-size: 1.1rem; } .action-label { flex: 1; font-size: 13px; font-weight: 500; } .action-arrow { color: var(--sp-text-muted); font-size: 0.8rem; }

    .stat-row { display: flex; justify-content: space-between; padding: 0.5rem 0; border-bottom: 1px solid var(--sp-border); font-size: 13px; }
  `],
})
export class ProjectSummaryComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private http = inject(HttpClient);
  data = signal<any>(null);

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get(`/projects/${id}/dashboard`).subscribe(d => this.data.set(d));
  }

  processSteps(d: any) {
    return [
      { id: 'budget', label: 'Orçamento', icon: 'calculator', route: 'budgets', status: d.planning?.hasBudget ? 'done' : 'pending', info: '' },
      { id: 'contract', label: 'Contrato', icon: 'file', route: 'contracts', status: d.planning?.hasContract ? 'done' : 'pending', info: '' },
      { id: 'schedule', label: 'Cronograma', icon: 'calendar', route: 'schedule', status: d.planning?.hasSchedule ? 'done' : 'pending', info: '' },
      { id: 'execution', label: 'Execução', icon: 'wrench', route: 'daily-logs', status: d.execution?.dailyLogs > 0 ? 'active' : 'pending', info: d.execution?.dailyLogs > 0 ? `${d.execution.dailyLogs} diários` : '' },
      { id: 'measurement', label: 'Medições', icon: 'chart-line', route: 'measurements', status: d.execution?.pendingMeasurements > 0 ? 'warning' : d.execution?.measurements > 0 ? 'active' : 'pending', info: d.execution?.pendingMeasurements > 0 ? `${d.execution.pendingMeasurements} pendentes` : '' },
      { id: 'procurement', label: 'Suprimentos', icon: 'truck', route: 'procurement', status: d.execution?.pendingOrders > 0 ? 'warning' : d.execution?.purchaseOrders > 0 ? 'done' : 'pending', info: d.execution?.pendingOrders > 0 ? `${d.execution.pendingOrders} pendentes` : '' },
      { id: 'finance', label: 'Financeiro', icon: 'wallet', route: 'finance', status: (d.financial?.totalPayable || 0) > 0 ? 'warning' : 'done', info: (d.financial?.totalPayable || 0) > 0 ? 'A pagar' : '' },
    ];
  }

  navigate(route: string) {
    this.router.navigate([route], { relativeTo: this.route.parent });
  }

  formatCurrency(v: number) {
    if (!v) return 'R$ 0';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL', maximumFractionDigits: 0 }).format(v);
  }
}
