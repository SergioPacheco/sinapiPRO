import { AfterViewInit, Component, NgZone, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { SettingsService } from '@core';
import { Subscription } from 'rxjs';

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
    this.loadAlerts();
  }

  ngAfterViewInit() {
    this.ngZone.runOutsideAngular(() => this.initCharts());
  }

  ngOnDestroy() {
    this.charts.forEach(c => c.destroy());
    this.sub.unsubscribe();
  }

  loadData() {
    this.http.get<any>('/projects?page=0&size=50').subscribe(res => {
      const list = res.content || [];
      this.projects.set(list);
      const active = list.filter((p: any) => p.status === 'IN_PROGRESS').length;
      const total = list.length;
      const budget = list.reduce((s: number, p: any) => s + (p.totalBudget || 0), 0);
      this.kpis.set([
        { icon: 'apartment', value: `${total}`, label: 'Obras Cadastradas', footer: `${active} em execução` },
        { icon: 'engineering', value: `${active}`, label: 'Em Execução', progress: total ? (active / total) * 100 : 0, footer: `${Math.round((active / Math.max(total, 1)) * 100)}% do portfólio` },
        { icon: 'payments', value: this.formatCurrency(budget), label: 'Valor Contratado', trend: 18, footer: 'Total do portfólio' },
        { icon: 'trending_up', value: '12,4%', label: 'Margem Prevista', trend: 2.1, footer: 'Média ponderada' },
        { icon: 'warning_amber', value: '3,2%', label: 'Desvio de Custo', trend: -0.8, footer: 'Vs. orçamento base' },
        { icon: 'event_available', value: '4', label: 'Próximas Medições', footer: 'Nos próximos 30 dias' },
      ]);
    });
  }

  loadAlerts() {
    this.alerts.set([
      { type: 'Estoque', description: 'Areia Média abaixo do mínimo (15/20 m³)', project: 'Parque das Flores', severity: 'warning' },
      { type: 'RFI', description: 'RFI #2 vencida — passagem tubulação pilar P12', project: 'Parque das Flores', severity: 'critical' },
      { type: 'Medição', description: 'Medição #3 aguardando aprovação', project: 'Parque das Flores', severity: 'info' },
      { type: 'Segurança', description: 'Inspeção de canteiro pendente', project: 'Hospital Regional', severity: 'warning' },
      { type: 'Contrato', description: 'Aditivo CTR-001 aguardando assinatura', project: 'Parque das Flores', severity: 'info' },
    ]);
  }

  openProject(id: string) { this.router.navigate(['/projects', id, 'summary']); }

  formatCurrency(value: number): string {
    if (value >= 1_000_000) return `R$ ${(value / 1_000_000).toFixed(1)}M`;
    if (value >= 1_000) return `R$ ${(value / 1_000).toFixed(0)}k`;
    return `R$ ${value.toFixed(0)}`;
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

    // Cashflow
    const c1 = new ApexCharts(document.querySelector('#chart-cashflow'), {
      ...base,
      chart: { ...base.chart, type: 'area', height: 280 },
      series: [
        { name: 'Receitas', data: [120, 180, 150, 220, 280, 310, 250, 290, 340, 380, 420, 450] },
        { name: 'Despesas', data: [90, 140, 130, 180, 200, 240, 210, 250, 280, 310, 350, 380] },
      ],
      xaxis: { categories: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'] },
      yaxis: { labels: { formatter: (v: number) => `R$ ${v}k` } },
      colors: ['#10b981', '#ef4444'],
      stroke: { curve: 'smooth', width: 2 },
      fill: { type: 'gradient', gradient: { opacityFrom: 0.35, opacityTo: 0.02 } },
      dataLabels: { enabled: false },
    });
    c1.render();

    // Status donut
    const c2 = new ApexCharts(document.querySelector('#chart-status'), {
      ...base,
      chart: { ...base.chart, type: 'donut', height: 280 },
      series: [2, 1, 0, 0],
      labels: ['Em Execução', 'Planejamento', 'Suspensa', 'Concluída'],
      colors: ['#10b981', '#3b82f6', '#f59e0b', '#6b7280'],
      plotOptions: { pie: { donut: { size: '72%', labels: { show: true, total: { show: true, label: 'Obras', fontSize: '13px' } } } } },
      legend: { position: 'bottom', fontSize: '12px' },
    });
    c2.render();

    // PV x Real
    const c3 = new ApexCharts(document.querySelector('#chart-pv-real'), {
      ...base,
      chart: { ...base.chart, type: 'line', height: 240 },
      series: [
        { name: 'Previsto', data: [50, 120, 210, 320, 450, 580, 720, 850] },
        { name: 'Realizado', data: [45, 110, 195, 310, 430, 560, 690, 820] },
      ],
      xaxis: { categories: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago'] },
      yaxis: { labels: { formatter: (v: number) => `R$ ${v}k` } },
      colors: ['#3b82f6', '#10b981'],
      stroke: { width: [2, 3], dashArray: [4, 0] },
      dataLabels: { enabled: false },
    });
    c3.render();

    // Costs by category
    const c4 = new ApexCharts(document.querySelector('#chart-costs'), {
      ...base,
      chart: { ...base.chart, type: 'bar', height: 240 },
      series: [{ name: 'Custo', data: [320, 180, 95, 72, 45] }],
      xaxis: { categories: ['Mão de Obra', 'Material', 'Equipamento', 'Subempreitada', 'Outros'] },
      yaxis: { labels: { formatter: (v: number) => `R$ ${v}k` } },
      colors: ['#6366f1'],
      plotOptions: { bar: { borderRadius: 4, horizontal: false, columnWidth: '55%' } },
      dataLabels: { enabled: false },
    });
    c4.render();

    this.charts = [c1, c2, c3, c4];
  }

  private updateChartsTheme() {
    const base = this.baseOpts();
    this.charts.forEach(c => c.updateOptions(base));
  }
}
