import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { StatusTagComponent, CurrencyDisplayComponent, EmptyStateComponent } from '../../shared/components';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, TableModule, ButtonModule, StatusTagComponent, CurrencyDisplayComponent, EmptyStateComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-4">
      <h2 style="margin:0">Dashboard</h2>
      <p-button label="Nova Obra" icon="pi pi-plus" (onClick)="router.navigate(['/projects/new'])" />
    </div>

    <!-- KPIs acionáveis -->
    <div class="grid mb-4">
      @for (kpi of kpis(); track kpi.label) {
        <div class="col-12 md:col-6 lg:col-3">
          <div class="kpi-card" [class.clickable]="kpi.route" (click)="kpi.route && router.navigate([kpi.route], {queryParams: kpi.params})">
            <div class="kpi-label">{{ kpi.label }}</div>
            <div class="kpi-value" [class.danger]="kpi.alert">{{ kpi.value }}</div>
            <div class="kpi-footer text-muted">{{ kpi.footer }}</div>
          </div>
        </div>
      }
    </div>

    <!-- Obras recentes com ações rápidas -->
    @if (projects().length > 0) {
      <h3 style="margin:0 0 0.75rem">Obras em andamento</h3>
      <p-table [value]="projects()" styleClass="p-datatable-sm p-datatable-striped" [rowHover]="true">
        <ng-template pTemplate="body" let-p>
          <tr style="cursor:pointer" (click)="router.navigate(['/projects', p.id, 'summary'])">
            <td style="width:90px" class="font-mono">{{ p.code }}</td>
            <td><strong>{{ p.name }}</strong><br><span class="text-muted" style="font-size:12px">{{ p.customerName }}</span></td>
            <td style="width:120px"><sp-status [status]="p.status" /></td>
            <td style="width:130px"><sp-currency [value]="p.totalBudget" /></td>
            <td style="width:60px"><i class="pi pi-chevron-right text-muted"></i></td>
          </tr>
        </ng-template>
      </p-table>
    } @else {
      <sp-empty title="Nenhuma obra cadastrada" message="Crie sua primeira obra para começar" icon="building" actionLabel="Nova Obra" (action)="router.navigate(['/projects/new'])" />
    }
  `,
  styles: [`
    .kpi-card { background:var(--sp-surface-card); border:1px solid var(--sp-border); border-radius:8px; padding:1.25rem; transition:border-color 0.15s; }
    .kpi-card.clickable { cursor:pointer; } .kpi-card.clickable:hover { border-color:var(--sp-primary); }
    .kpi-label { font-size:11px; color:var(--sp-text-muted); text-transform:uppercase; letter-spacing:0.4px; }
    .kpi-value { font-size:1.75rem; font-weight:700; margin:4px 0; } .kpi-value.danger { color:var(--sp-danger); }
    .kpi-footer { font-size:12px; }
  `],
})
export class DashboardComponent implements OnInit {
  private http = inject(HttpClient);
  router = inject(Router);
  kpis = signal<any[]>([]);
  projects = signal<any[]>([]);

  ngOnInit() {
    this.http.get<any>('/projects?page=0&size=10').subscribe(res => {
      const list = res.content || [];
      this.projects.set(list.filter((p: any) => p.status === 'IN_PROGRESS' || p.status === 'PLANNING'));

      const active = list.filter((p: any) => p.status === 'IN_PROGRESS').length;
      const budget = list.reduce((s: number, p: any) => s + (p.totalBudget || 0), 0);

      this.kpis.set([
        { label: 'Obras Ativas', value: `${active}`, footer: `${list.length} total`, route: '/projects', params: { status: 'IN_PROGRESS' } },
        { label: 'Valor Contratado', value: this.fmt(budget), footer: 'Portfólio total', route: null, params: {} },
        { label: 'Medições Pendentes', value: '—', footer: 'Aguardando aprovação', route: null, params: {}, alert: false },
        { label: 'Vencendo Hoje', value: '—', footer: 'Contas a pagar', route: null, params: {}, alert: false },
      ]);
    });

    // Load real pending counts
    this.http.get<any>('/notifications?read=false').subscribe({
      next: notifs => {
        const pending = (notifs || []).filter((n: any) => n.type === 'MEASUREMENT_SUBMITTED').length;
        const overdue = (notifs || []).filter((n: any) => n.severity === 'WARNING').length;
        this.kpis.update(k => k.map((kpi, i) => {
          if (i === 2) return { ...kpi, value: `${pending}`, alert: pending > 0 };
          if (i === 3) return { ...kpi, value: `${overdue}`, alert: overdue > 0 };
          return kpi;
        }));
      },
      error: () => {},
    });
  }

  private fmt(v: number): string {
    if (v >= 1_000_000) return `R$ ${(v / 1_000_000).toFixed(1)}M`;
    if (v >= 1_000) return `R$ ${(v / 1_000).toFixed(0)}k`;
    return `R$ ${v.toFixed(0)}`;
  }
}
