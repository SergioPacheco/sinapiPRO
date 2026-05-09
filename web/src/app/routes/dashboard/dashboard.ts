import { AfterViewInit, Component, NgZone, OnDestroy, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { SettingsService } from '@core';
import { MtxProgressModule } from '@ng-matero/extensions/progress';
import { PageHeader } from '@shared';
import { Subscription } from 'rxjs';
import { CHARTS, STATS } from './data';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatListModule,
    MatTableModule,
    MatIconModule,
    MtxProgressModule,
    PageHeader,
  ],
})
export class Dashboard implements OnInit, AfterViewInit, OnDestroy {
  private readonly ngZone = inject(NgZone);
  private readonly settings = inject(SettingsService);

  stats = STATS;
  charts = CHARTS;
  chart1?: ApexCharts;
  chart2?: ApexCharts;

  notifySubscription = Subscription.EMPTY;

  // Medições recentes
  measurementColumns = ['number', 'budget', 'amount', 'status'];
  recentMeasurements = [
    { number: 5, budget: 'Residencial Aurora', amount: 'R$ 185.420,00', status: 'APPROVED', statusLabel: 'Aprovada', statusClass: 'bg-green-90' },
    { number: 3, budget: 'Comercial Centro', amount: 'R$ 92.800,00', status: 'SUBMITTED', statusLabel: 'Enviada', statusClass: 'bg-orange-90' },
    { number: 8, budget: 'Industrial Norte', amount: 'R$ 340.150,00', status: 'DRAFT', statusLabel: 'Rascunho', statusClass: 'bg-blue-90' },
    { number: 2, budget: 'Reforma Escola', amount: 'R$ 45.600,00', status: 'PAID', statusLabel: 'Paga', statusClass: '' },
  ];

  // Pedidos pendentes
  pendingOrders = [
    { description: 'Cimento CP-II 50kg (200 sacos)', supplier: 'Votorantim', amount: 'R$ 7.800,00', icon: 'local_shipping', iconClass: 'text-orange' },
    { description: 'Aço CA-50 10mm (2 ton)', supplier: 'Gerdau', amount: 'R$ 14.200,00', icon: 'local_shipping', iconClass: 'text-orange' },
    { description: 'Tubos PVC 100mm (50 barras)', supplier: 'Tigre', amount: 'R$ 3.450,00', icon: 'inventory', iconClass: 'text-blue' },
  ];

  // Alertas
  alerts = [
    { title: 'Medição #3 aguardando aprovação', description: 'Comercial Centro — há 3 dias', icon: 'warning', color: 'warn' },
    { title: 'Contrato vencendo em 15 dias', description: 'Industrial Norte — Terraplanagem', icon: 'event', color: 'accent' },
    { title: 'Estoque baixo: Cimento CP-II', description: 'Apenas 20 sacos restantes', icon: 'inventory_2', color: 'warn' },
  ];

  get isDark() {
    return this.settings.getThemeColor() == 'dark';
  }

  ngOnInit() {
    this.notifySubscription = this.settings.notify.subscribe(() => this.updateCharts());
  }

  ngAfterViewInit() {
    this.ngZone.runOutsideAngular(() => this.initCharts());
  }

  ngOnDestroy() {
    this.chart1?.destroy();
    this.chart2?.destroy();
    this.notifySubscription.unsubscribe();
  }

  initCharts() {
    this.chart1 = new ApexCharts(document.querySelector('#chart1'), this.charts[0]);
    this.chart1?.render();
    this.chart2 = new ApexCharts(document.querySelector('#chart2'), this.charts[1]);
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
    this.chart2?.updateOptions({
      ...opts,
      plotOptions: {
        radar: {
          polygons: {
            strokeColors: this.isDark ? '#5a5a5a' : '#e1e1e1',
            connectorColors: this.isDark ? '#5a5a5a' : '#e1e1e1',
            fill: { colors: this.isDark ? ['#2c2c2c', '#222'] : ['#f2f2f2', '#fff'] },
          },
        },
      },
    });
  }
}
