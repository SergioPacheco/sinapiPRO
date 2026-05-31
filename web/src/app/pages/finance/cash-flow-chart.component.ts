interface CashFlowProjection { month: string; inflow: number; outflow: number; balance: number; accumulated: number; }

import { Component, inject, OnInit, ElementRef, viewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as echarts from 'echarts';

@Component({
  selector: 'app-cash-flow-chart',
  standalone: true,
  template: `<div #chart class="chart-container"></div>`,
  styles: [`.chart-container { width: 100%; height: 450px; background: #16213e; border-radius: 10px; border: 1px solid #2a2a4a; }`]
})
export class CashFlowChartComponent implements OnInit {
  private http = inject(HttpClient);
  chartEl = viewChild<ElementRef>('chart');
  private chart: echarts.ECharts | null = null;

  ngOnInit() {
    this.http.get<CashFlowProjection[]>(`/finance/cash-flow/projection`).subscribe(data => {
      setTimeout(() => this.render(data), 100);
    });
  }

  private render(data: CashFlowProjection[]) {
    const el = this.chartEl()?.nativeElement;
    if (!el) return;
    if (!this.chart) this.chart = echarts.init(el, 'dark');

    const months = data.map(d => d.month);

    this.chart.setOption({
      title: { text: 'Fluxo de Caixa — Projeção 12 meses', left: 'center', textStyle: { color: '#e0e0e0', fontSize: 14 } },
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      legend: { bottom: 0, textStyle: { color: '#8a8aaa' } },
      grid: { left: 70, right: 30, top: 50, bottom: 60 },
      xAxis: { type: 'category', data: months, axisLabel: { color: '#8a8aaa' } },
      yAxis: { type: 'value', axisLabel: { color: '#8a8aaa', formatter: (v: number) => `R$ ${(v / 1000).toFixed(0)}k` } },
      series: [
        { name: 'Entradas', type: 'bar', stack: 'flow', data: data.map(d => d.inflow), itemStyle: { color: '#66bb6a' } },
        { name: 'Saídas', type: 'bar', stack: 'flow', data: data.map(d => -d.outflow), itemStyle: { color: '#ef5350' } },
        { name: 'Saldo Acumulado', type: 'line', data: data.map(d => d.accumulated), lineStyle: { color: '#4fc3f7', width: 3 }, symbol: 'circle', symbolSize: 6,
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(79,195,247,0.3)' }, { offset: 1, color: 'rgba(79,195,247,0)' }]) }
        }
      ]
    });
  }
}
