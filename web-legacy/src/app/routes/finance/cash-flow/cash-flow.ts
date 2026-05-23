import { AfterViewInit, Component, inject, NgZone, OnDestroy, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';
import { FinanceService } from '../services/finance.service';
import { CashFlowSummary, BudgetVsActualLine } from '../models/finance.model';

@Component({
  selector: 'app-cash-flow',
  template: `
    <page-header title="Financeiro" subtitle="Fluxo de caixa e orçado x realizado">
      <button mat-stroked-button (click)="downloadBvaReport()">
        <mat-icon>picture_as_pdf</mat-icon> Previsto×Realizado PDF
      </button>
    </page-header>

    <!-- KPI Cards -->
    <div class="finance-kpis">
      <mat-card class="kpi green">
        <mat-icon>trending_up</mat-icon>
        <div><span class="value">{{ formatCurrency(summary()?.receivablesReceived || 0) }}</span><span class="label">Recebido</span></div>
      </mat-card>
      <mat-card class="kpi red">
        <mat-icon>trending_down</mat-icon>
        <div><span class="value">{{ formatCurrency(summary()?.payablesPaid || 0) }}</span><span class="label">Pago</span></div>
      </mat-card>
      <mat-card class="kpi blue">
        <mat-icon>account_balance_wallet</mat-icon>
        <div><span class="value">{{ formatCurrency(summary()?.currentBalance || 0) }}</span><span class="label">Saldo Atual</span></div>
      </mat-card>
      <mat-card class="kpi orange">
        <mat-icon>schedule</mat-icon>
        <div><span class="value">{{ formatCurrency(summary()?.projectedBalance || 0) }}</span><span class="label">Projeção</span></div>
      </mat-card>
    </div>

    <!-- Chart -->
    <mat-card class="chart-card">
      <h3>Orçado x Realizado x Comprometido</h3>
      <div id="financeChart"></div>
    </mat-card>

    <!-- Table -->
    <mat-card>
      <h3 style="padding:16px 16px 0">Detalhamento por Centro de Custo</h3>
      <mtx-grid [columns]="bvaColumns" [data]="bvaLines()" [loading]="isLoading()" [rowStriped]="true"
        [pageOnFront]="true" [showPaginator]="false" />
    </mat-card>
  `,
  styles: `
    .finance-kpis { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 12px; margin-bottom: 20px; }
    .kpi { display: flex; align-items: center; gap: 12px; padding: 16px; }
    .kpi mat-icon { font-size: 32px; width: 32px; height: 32px; }
    .kpi .value { font-size: 22px; font-weight: 700; display: block; }
    .kpi .label { font-size: 12px; color: var(--mat-sys-on-surface-variant); }
    .kpi.green mat-icon { color: #4caf50; } .kpi.red mat-icon { color: #f44336; }
    .kpi.blue mat-icon { color: #1976d2; } .kpi.orange mat-icon { color: #ff9800; }
    .chart-card { padding: 16px; margin-bottom: 16px; }
    .chart-card h3 { margin: 0 0 8px; font-size: 16px; font-weight: 500; }
  `,
  imports: [MatCardModule, MatIconModule, MatTabsModule, MtxGridModule, PageHeader],
})
export class CashFlowComponent implements OnInit, AfterViewInit, OnDestroy {
  private readonly service = inject(FinanceService);
  private readonly route = inject(ActivatedRoute);
  private readonly ngZone = inject(NgZone);
  private projectId = '';
  private chart?: ApexCharts;

  summary = signal<CashFlowSummary | null>(null);
  bvaLines = signal<BudgetVsActualLine[]>([]);
  isLoading = signal(true);

  bvaColumns: MtxGridColumn[] = [
    { header: 'Código', field: 'code', width: '90px' },
    { header: 'Descrição', field: 'name' },
    { header: 'Orçado', field: 'budgeted', width: '120px', formatter: (d: BudgetVsActualLine) => this.formatCurrency(d.budgeted) },
    { header: 'Comprometido', field: 'committed', width: '120px', formatter: (d: BudgetVsActualLine) => this.formatCurrency(d.committed) },
    { header: 'Realizado', field: 'actual', width: '120px', formatter: (d: BudgetVsActualLine) => this.formatCurrency(d.actual) },
    { header: 'Saldo', field: 'variance', width: '120px', formatter: (d: BudgetVsActualLine) => this.formatCurrency(d.variance) },
    { header: '% Exec.', field: 'pctExecuted', width: '80px', formatter: (d: BudgetVsActualLine) => `${(d.pctExecuted || 0).toFixed(1)}%` },
  ];

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.service.cashFlowSummary(this.projectId).subscribe(s => this.summary.set(s));
    this.service.budgetVsActual(this.projectId).subscribe({
      next: r => { this.bvaLines.set(r.lines || []); this.isLoading.set(false); this.renderChart(r.lines || []); },
      error: () => this.isLoading.set(false),
    });
  }

  ngAfterViewInit() {}

  ngOnDestroy() { this.chart?.destroy(); }

  renderChart(lines: BudgetVsActualLine[]) {
    if (!lines.length) return;
    this.ngZone.runOutsideAngular(() => {
      this.chart = new ApexCharts(document.querySelector('#financeChart'), {
        chart: { type: 'bar', height: 300, toolbar: { show: false }, animations: { enabled: true } },
        series: [
          { name: 'Orçado', data: lines.map(l => l.budgeted) },
          { name: 'Realizado', data: lines.map(l => l.actual) },
          { name: 'Comprometido', data: lines.map(l => l.committed) },
        ],
        xaxis: { categories: lines.map(l => l.code || l.name?.substring(0, 10)) },
        colors: ['#1976d2', '#4caf50', '#ff9800'],
        plotOptions: { bar: { borderRadius: 4, columnWidth: '70%' } },
        dataLabels: { enabled: false },
        legend: { position: 'top' },
        yaxis: { labels: { formatter: (v: number) => this.formatCurrency(v) } },
      });
      this.chart.render();
    });
  }

  formatCurrency(v: number): string {
    if (!v) return 'R$ 0';
    if (Math.abs(v) >= 1_000_000) return `R$ ${(v / 1_000_000).toFixed(1)}M`;
    if (Math.abs(v) >= 1_000) return `R$ ${(v / 1_000).toFixed(0)}k`;
    return `R$ ${v.toFixed(0)}`;
  }

  downloadBvaReport() {
    const http = inject(HttpClient);
    const projectId = this.route.parent?.parent?.snapshot.paramMap.get('projectId') || '';
    if (!projectId) return;
    http.get(`/projects/${projectId}/finance/budget-vs-actual/reports/report.pdf`, { responseType: 'blob' })
      .subscribe(blob => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = 'previsto-realizado.pdf';
        a.click();
        URL.revokeObjectURL(a.href);
      });
  }
}
