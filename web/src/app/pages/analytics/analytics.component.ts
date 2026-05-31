import { Component, inject, OnInit, signal, ElementRef, viewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe } from '@angular/common';
import * as echarts from 'echarts';

interface EvmData { pv: number; ev: number; ac: number; cpi: number; spi: number; eac: number; vac: number; percentComplete: number; }
interface DreRow { category: string; budgeted: number; actual: number; variance: number; }
interface PortfolioProject { id: string; name: string; cpi: number; spi: number; percentComplete: number; totalBudget: number; }

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [DecimalPipe],
  template: `
    <div class="analytics">
      <div class="tabs">
        <button [class.active]="tab() === 'evm'" (click)="tab.set('evm')">EVM</button>
        <button [class.active]="tab() === 'dre'" (click)="tab.set('dre')">DRE</button>
        <button [class.active]="tab() === 'portfolio'" (click)="tab.set('portfolio')">Portfólio</button>
      </div>

      @switch (tab()) {
        @case ('evm') {
          @if (evm(); as e) {
            <div class="kpi-row">
              <div class="kpi"><span class="label">CPI</span><span class="val" [class.good]="e.cpi>=1" [class.bad]="e.cpi<1">{{ e.cpi | number:'1.2-2' }}</span></div>
              <div class="kpi"><span class="label">SPI</span><span class="val" [class.good]="e.spi>=1" [class.bad]="e.spi<1">{{ e.spi | number:'1.2-2' }}</span></div>
              <div class="kpi"><span class="label">EAC</span><span class="val">R$ {{ e.eac | number:'1.0-0' }}</span></div>
              <div class="kpi"><span class="label">VAC</span><span class="val" [class.good]="e.vac>=0" [class.bad]="e.vac<0">R$ {{ e.vac | number:'1.0-0' }}</span></div>
              <div class="kpi"><span class="label">% Concluído</span><span class="val">{{ e.percentComplete | number:'1.0-0' }}%</span></div>
            </div>
            <div #evmChart class="chart"></div>
          }
        }
        @case ('dre') {
          <table class="dre-table">
            <thead><tr><th>Categoria</th><th>Orçado</th><th>Realizado</th><th>Variação</th></tr></thead>
            <tbody>
              @for (row of dre(); track row.category) {
                <tr>
                  <td>{{ row.category }}</td>
                  <td class="num">R$ {{ row.budgeted | number:'1.2-2' }}</td>
                  <td class="num">R$ {{ row.actual | number:'1.2-2' }}</td>
                  <td class="num" [class.good]="row.variance>=0" [class.bad]="row.variance<0">R$ {{ row.variance | number:'1.2-2' }}</td>
                </tr>
              }
            </tbody>
          </table>
        }
        @case ('portfolio') {
          <table class="portfolio-table">
            <thead><tr><th>Obra</th><th>CPI</th><th>SPI</th><th>% Concluído</th><th>Orçamento</th></tr></thead>
            <tbody>
              @for (p of portfolio(); track p.id) {
                <tr>
                  <td>{{ p.name }}</td>
                  <td [class.good]="p.cpi>=1" [class.bad]="p.cpi<1">{{ p.cpi | number:'1.2-2' }}</td>
                  <td [class.good]="p.spi>=1" [class.bad]="p.spi<1">{{ p.spi | number:'1.2-2' }}</td>
                  <td>{{ p.percentComplete | number:'1.0-0' }}%</td>
                  <td class="num">R$ {{ p.totalBudget | number:'1.0-0' }}</td>
                </tr>
              }
            </tbody>
          </table>
        }
      }
    </div>
  `,
  styles: [`
    .tabs { display: flex; gap: 0; margin-bottom: 1.5rem; border-bottom: 1px solid #2a2a4a;
      button { padding: 0.75rem 1.5rem; background: transparent; border: none; color: #8a8aaa; cursor: pointer; border-bottom: 2px solid transparent;
        &.active { color: #4fc3f7; border-bottom-color: #4fc3f7; } } }
    .kpi-row { display: grid; grid-template-columns: repeat(5, 1fr); gap: 1rem; margin-bottom: 1.5rem; }
    .kpi { background: #16213e; border-radius: 10px; padding: 1.25rem; border: 1px solid #2a2a4a; text-align: center;
      .label { display: block; font-size: 0.75rem; color: #8a8aaa; text-transform: uppercase; }
      .val { display: block; font-size: 1.6rem; font-weight: 700; margin-top: 0.25rem; } }
    .good { color: #66bb6a; } .bad { color: #ef5350; }
    .chart { height: 350px; background: #16213e; border-radius: 10px; border: 1px solid #2a2a4a; }
    table { width: 100%; border-collapse: collapse; background: #16213e; border-radius: 10px; overflow: hidden; }
    th { text-align: left; padding: 0.75rem 1rem; color: #8a8aaa; font-size: 0.8rem; border-bottom: 1px solid #2a2a4a; }
    td { padding: 0.75rem 1rem; border-bottom: 1px solid #1a1a2e; }
    .num { text-align: right; font-family: monospace; }
  `]
})
export class AnalyticsComponent implements OnInit {
  private http = inject(HttpClient);
  evmChartEl = viewChild<ElementRef>('evmChart');
  tab = signal<'evm' | 'dre' | 'portfolio'>('evm');
  evm = signal<EvmData | null>(null);
  dre = signal<DreRow[]>([]);
  portfolio = signal<PortfolioProject[]>([]);

  ngOnInit() {
    this.http.get<EvmData>('/analytics/evm').subscribe(d => { this.evm.set(d); setTimeout(() => this.renderEvmChart(d), 200); });
    this.http.get<DreRow[]>('/analytics/dre').subscribe(d => this.dre.set(d));
    this.http.get<PortfolioProject[]>('/analytics/portfolio').subscribe(d => this.portfolio.set(d));
  }

  private renderEvmChart(d: EvmData) {
    const el = this.evmChartEl()?.nativeElement;
    if (!el) return;
    const chart = echarts.init(el, 'dark');
    chart.setOption({
      tooltip: { trigger: 'item' },
      series: [{ type: 'gauge', min: 0, max: 2, splitNumber: 4, detail: { formatter: '{value}' },
        data: [{ value: d.cpi, name: 'CPI' }, { value: d.spi, name: 'SPI' }],
        axisLine: { lineStyle: { width: 20, color: [[0.5, '#ef5350'], [1, '#ffa726'], [2, '#66bb6a']] } }
      }]
    });
  }
}
