import { AfterViewInit, Component, NgZone, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { SettingsService } from '@core';
import { Subscription, forkJoin } from 'rxjs';

interface ProjectSummary {
  id: string; code: string; name: string; status: string; customerName: string;
  city?: string; state?: string; totalBudget: number; startDate: string;
}

interface Kpi {
  icon: string; value: string; label: string; trend?: number;
  progress?: number; footer?: string;
}

interface Alert {
  type: string; description: string; project: string; severity: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  imports: [MatButtonModule, MatIconModule, RouterLink],
})
export class Dashboard implements OnInit, AfterViewInit, OnDestroy {
  private readonly ngZone = inject(NgZone);
  private readonly settings = inject(SettingsService);
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  projects = signal<ProjectSummary[]>([]);
  kpis = signal<Kpi[]>([]);
  alerts = signal<Alert[]>([]);

  private charts: ApexCharts[] = [];
  private sub = Subscription.EMPTY;

  statusMap: Record<string, { label: string }> = {
    PLANNING: { label: 'Planejamento' },
    IN_PROGRESS: { label: 'Em Execução' },
    SUSPENDED: { label: 'Suspensa' },
    COMPLETED: { label: 'Concluída' },
  };

  get isDark() { return this.settings.getThemeColor() === 'dark'; }

  ngOnInit() {
    this.sub = this.settings.notify.subscribe(() => this.updateChartsTheme());
    this.loadData();
  }

  ngAfterViewInit() {
    this.ngZone.runOutsideAngular(() => this.initCharts());
  }

  ngOnDestroy() {
    this.charts.forEach(c => c.destroy());
    this.sub.unsubscribe();
  }

  loadData() {
    forkJoin({
      projects: this.http.get<any>('/projects?page=0&size=50'),
      portfolio: this.http.get<any>('/analytics/portfolio'),
      notifications: this.http.get<any[]>('/notifications'),
    }).subscribe(({ projects, portfolio, notifications }) => {
      const list = projects.content || [];
      this.projects.set(list);

      const active = list.filter((p: any) => p.status === 'IN_PROGRESS').length;
      const total = list.length;
      const budget = list.reduce((s: number, p: any) => s + (p.totalBudget || 0), 0);
      const planning = list.filter((p: any) => p.status === 'PLANNING').length;

      this.kpis.set([
        { icon: 'apartment', value: `${total}`, label: 'Obras Cadastradas', footer: `${planning} em planejamento` },
        { icon: 'engineering', value: `${active}`, label: 'Em Execução', progress: total ? (active / total) * 100 : 0, footer: `${Math.round((active / Math.max(total, 1)) * 100)}% do portfólio` },
        { icon: 'payments', value: this.formatCurrency(budget), label: 'Valor Contratado', footer: 'Total do portfólio' },
        { icon: 'store', value: `${portfolio.activeSuppliers || 0}`, label: 'Fornecedores Ativos', footer: 'Cadastrados no sistema' },
        { icon: 'receipt', value: `${portfolio.totalInvoices || 0}`, label: 'Notas Fiscais', footer: 'Total registrado' },
        { icon: 'notifications_active', value: `${notifications.filter((n: any) => !n.read).length}`, label: 'Pendências', footer: 'Notificações não lidas' },
      ]);

      // Alertas reais das notificações
      this.alerts.set(
        notifications
          .filter((n: any) => !n.read)
          .slice(0, 8)
          .map((n: any) => ({
            type: this.alertTypeLabel(n.type),
            description: n.title,
            project: n.budgetId ? list.find((p: any) => p.id === n.budgetId)?.name || '—' : '—',
            severity: n.severity?.toLowerCase() || 'info',
          }))
      );
    });
  }

  openProject(id: string) { this.router.navigate(['/projects', id, 'summary']); }

  formatCurrency(value: number): string {
    if (value >= 1_000_000) return `R$ ${(value / 1_000_000).toFixed(1)}M`;
    if (value >= 1_000) return `R$ ${(value / 1_000).toFixed(0)}k`;
    return `R$ ${value.toFixed(0)}`;
  }

  private alertTypeLabel(type: string): string {
    const map: Record<string, string> = {
      MEASUREMENT_SUBMITTED: 'Medição',
      STOCK_LOW: 'Estoque',
      RFI_OVERDUE: 'RFI',
      SYSTEM: 'Sistema',
    };
    return map[type] || type;
  }

  private baseOpts() {
    const fg = this.isDark ? '#b0b8c4' : '#4a5568';
    const grid = this.isDark ? '#2d3748' : '#e2e8f0';
    return {
      chart: { foreColor: fg, background: 'transparent', toolbar: { show: false }, animations: { speed: 600 } },
      grid: { borderColor: grid, strokeDashArray: 3 },
      tooltip: { theme: this.isDark ? 'dark' : 'light' },
      theme: { mode: (this.isDark ? 'dark' : 'light') as 'dark' | 'light' },
    };
  }

  initCharts() {
    const base = this.baseOpts();
    const projects = this.projects();
    const statusCounts = {
      inProgress: projects.filter(p => p.status === 'IN_PROGRESS').length,
      planning: projects.filter(p => p.status === 'PLANNING').length,
      suspended: projects.filter(p => p.status === 'SUSPENDED').length,
      completed: projects.filter(p => p.status === 'COMPLETED').length,
    };

    // Status donut — dados reais
    const c1 = new ApexCharts(document.querySelector('#chart-status'), {
      ...base,
      chart: { ...base.chart, type: 'donut', height: 280 },
      series: [statusCounts.inProgress, statusCounts.planning, statusCounts.suspended, statusCounts.completed],
      labels: ['Em Execução', 'Planejamento', 'Suspensa', 'Concluída'],
      colors: ['#10b981', '#3b82f6', '#f59e0b', '#6b7280'],
      plotOptions: { pie: { donut: { size: '72%', labels: { show: true, total: { show: true, label: 'Obras', fontSize: '13px' } } } } },
      legend: { position: 'bottom', fontSize: '12px' },
    });
    c1.render();

    // Cash flow — carrega dados reais se houver projeto ativo
    const activeProject = projects.find(p => p.status === 'IN_PROGRESS');
    if (activeProject) {
      this.http.get<any>(`/projects/${activeProject.id}/finance/cash-flow/projection`).subscribe({
        next: data => {
          const months = (data.months || []).map((m: any) => m.month);
          const income = (data.months || []).map((m: any) => Number(m.income) / 1000);
          const expense = (data.months || []).map((m: any) => Number(m.expense) / 1000);
          const c2 = new ApexCharts(document.querySelector('#chart-cashflow'), {
            ...base,
            chart: { ...base.chart, type: 'area', height: 280 },
            series: [{ name: 'Receitas', data: income }, { name: 'Despesas', data: expense }],
            xaxis: { categories: months.length ? months : ['—'] },
            yaxis: { labels: { formatter: (v: number) => `R$ ${v.toFixed(0)}k` } },
            colors: ['#10b981', '#ef4444'],
            stroke: { curve: 'smooth', width: 2 },
            fill: { type: 'gradient', gradient: { opacityFrom: 0.35, opacityTo: 0.02 } },
            dataLabels: { enabled: false },
          });
          c2.render();
          this.charts.push(c2);
        },
        error: () => this.renderEmptyCashflow(base),
      });
    } else {
      this.renderEmptyCashflow(base);
    }

    this.charts.push(c1);
  }

  private renderEmptyCashflow(base: any) {
    const c = new ApexCharts(document.querySelector('#chart-cashflow'), {
      ...base,
      chart: { ...base.chart, type: 'area', height: 280 },
      series: [{ name: 'Receitas', data: [0] }, { name: 'Despesas', data: [0] }],
      xaxis: { categories: ['Sem dados'] },
      colors: ['#10b981', '#ef4444'],
      stroke: { curve: 'smooth', width: 2 },
      dataLabels: { enabled: false },
      noData: { text: 'Sem dados de fluxo de caixa' },
    });
    c.render();
    this.charts.push(c);
  }

  private updateChartsTheme() {
    const base = this.baseOpts();
    this.charts.forEach(c => c.updateOptions(base));
  }
}
