import { AfterViewInit, Component, NgZone, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatRippleModule } from '@angular/material/core';
import { SettingsService } from '@core';
import { MtxProgressModule } from '@ng-matero/extensions/progress';
import { PageHeader } from '@shared';
import { Subscription, forkJoin } from 'rxjs';

interface ProjectSummary {
  id: string; code: string; name: string; status: string; customerName: string;
  totalBudget: number; startDate: string; expectedEndDate: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  imports: [
    MatButtonModule, MatCardModule, MatChipsModule, MatListModule, MatTableModule,
    MatIconModule, MatProgressBarModule, MatRippleModule, MtxProgressModule, PageHeader,
  ],
})
export class Dashboard implements OnInit, AfterViewInit, OnDestroy {
  private readonly ngZone = inject(NgZone);
  private readonly settings = inject(SettingsService);
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  projects = signal<ProjectSummary[]>([]);
  totalProjects = signal(0);
  totalBudget = signal(0);
  activeProjects = signal(0);

  chart1?: ApexCharts;
  chart2?: ApexCharts;
  notifySubscription = Subscription.EMPTY;

  statusMap: Record<string, { label: string; color: string; icon: string }> = {
    PLANNING: { label: 'Planejamento', color: '#2196f3', icon: 'edit_note' },
    IN_PROGRESS: { label: 'Em Execução', color: '#4caf50', icon: 'engineering' },
    SUSPENDED: { label: 'Suspensa', color: '#ff9800', icon: 'pause_circle' },
    COMPLETED: { label: 'Concluída', color: '#9e9e9e', icon: 'check_circle' },
  };

  get isDark() { return this.settings.getThemeColor() == 'dark'; }

  ngOnInit() {
    this.notifySubscription = this.settings.notify.subscribe(() => this.updateCharts());
    this.loadData();
  }

  ngAfterViewInit() {
    this.ngZone.runOutsideAngular(() => this.initCharts());
  }

  ngOnDestroy() {
    this.chart1?.destroy();
    this.chart2?.destroy();
    this.notifySubscription.unsubscribe();
  }

  loadData() {
    this.http.get<any>('/projects?page=0&size=50').subscribe(res => {
      this.projects.set(res.content);
      this.totalProjects.set(res.totalElements);
      this.activeProjects.set(res.content.filter((p: any) => p.status === 'IN_PROGRESS').length);
      this.totalBudget.set(res.content.reduce((sum: number, p: any) => sum + (p.totalBudget || 0), 0));
    });
  }

  openProject(id: string) {
    this.router.navigate(['/projects', id, 'summary']);
  }

  formatCurrency(value: number): string {
    if (value >= 1_000_000) return `R$ ${(value / 1_000_000).toFixed(1)}M`;
    if (value >= 1_000) return `R$ ${(value / 1_000).toFixed(0)}k`;
    return `R$ ${value.toFixed(0)}`;
  }

  initCharts() {
    this.chart1 = new ApexCharts(document.querySelector('#chart1'), {
      chart: { type: 'area', height: 300, toolbar: { show: false }, animations: { enabled: true, speed: 800 } },
      series: [
        { name: 'Receitas', data: [120, 180, 150, 220, 280, 310, 250, 290, 340, 380, 420, 450] },
        { name: 'Despesas', data: [90, 140, 130, 180, 200, 240, 210, 250, 280, 310, 350, 380] },
      ],
      xaxis: { categories: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'] },
      yaxis: { labels: { formatter: (v: number) => `R$ ${v}k` } },
      colors: ['#4caf50', '#f44336'],
      stroke: { curve: 'smooth', width: 2 },
      fill: { type: 'gradient', gradient: { opacityFrom: 0.5, opacityTo: 0.05 } },
      dataLabels: { enabled: false },
    });
    this.chart1?.render();

    this.chart2 = new ApexCharts(document.querySelector('#chart2'), {
      chart: { type: 'donut', height: 300, animations: { enabled: true, speed: 800 } },
      series: [3, 1, 1, 0],
      labels: ['Em Execução', 'Planejamento', 'Suspensa', 'Concluída'],
      colors: ['#4caf50', '#2196f3', '#ff9800', '#9e9e9e'],
      plotOptions: { pie: { donut: { size: '70%', labels: { show: true, total: { show: true, label: 'Obras', fontSize: '14px' } } } } },
      legend: { position: 'bottom' },
    });
    this.chart2?.render();
    this.updateCharts();
  }

  updateCharts() {
    const opts = {
      chart: { foreColor: this.isDark ? '#ccc' : '#333', background: 'transparent' },
      tooltip: { theme: this.isDark ? 'dark' : 'light' },
      grid: { borderColor: this.isDark ? '#5a5a5a' : '#e1e1e1' },
      theme: { mode: this.isDark ? 'dark' : 'light' },
    };
    this.chart1?.updateOptions(opts);
    this.chart2?.updateOptions(opts);
  }
}
