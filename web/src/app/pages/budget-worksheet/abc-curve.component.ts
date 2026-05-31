import { Component, Input, OnChanges, ElementRef, viewChild } from '@angular/core';
import * as echarts from 'echarts';
import { BudgetItem } from '../../shared/models/api.models';

@Component({
  selector: 'app-abc-curve',
  standalone: true,
  template: `<div #chart class="chart-container"></div>`,
  styles: [`.chart-container { width: 100%; height: 400px; background: #16213e; border-radius: 10px; border: 1px solid #2a2a4a; }`]
})
export class AbcCurveComponent implements OnChanges {
  @Input() items: BudgetItem[] = [];
  chartEl = viewChild<ElementRef>('chart');

  private chart: echarts.ECharts | null = null;

  ngOnChanges() {
    if (!this.items.length) return;
    setTimeout(() => this.render(), 100);
  }

  private render() {
    const el = this.chartEl()?.nativeElement;
    if (!el) return;
    if (!this.chart) this.chart = echarts.init(el, 'dark');

    // Sort by total cost descending
    const sorted = [...this.items].sort((a, b) => b.totalWithBdi - a.totalWithBdi);
    const total = sorted.reduce((sum, i) => sum + i.totalWithBdi, 0);

    // Calculate cumulative %
    let cumulative = 0;
    const data = sorted.map(item => {
      cumulative += item.totalWithBdi;
      return { name: item.compositionCode, value: item.totalWithBdi, cumPct: (cumulative / total) * 100 };
    });

    // ABC classification: A=80%, B=95%, C=100%
    const categories = data.map(d => d.name);
    const values = data.map(d => d.value);
    const cumPcts = data.map(d => d.cumPct);
    const colors = data.map(d => d.cumPct <= 80 ? '#ef5350' : d.cumPct <= 95 ? '#ffa726' : '#66bb6a');

    this.chart.setOption({
      title: { text: 'Curva ABC', left: 'center', textStyle: { color: '#e0e0e0', fontSize: 14 } },
      tooltip: { trigger: 'axis' },
      grid: { left: 60, right: 60, bottom: 80 },
      xAxis: { type: 'category', data: categories, axisLabel: { rotate: 45, fontSize: 9, color: '#8a8aaa' } },
      yAxis: [
        { type: 'value', name: 'Custo (R$)', axisLabel: { color: '#8a8aaa' } },
        { type: 'value', name: '% Acumulado', max: 100, axisLabel: { formatter: '{value}%', color: '#8a8aaa' } }
      ],
      series: [
        { type: 'bar', data: values.map((v, i) => ({ value: v, itemStyle: { color: colors[i] } })), yAxisIndex: 0 },
        { type: 'line', data: cumPcts, yAxisIndex: 1, lineStyle: { color: '#4fc3f7', width: 2 }, symbol: 'none',
          markLine: { data: [{ yAxis: 80, label: { formatter: 'A (80%)' } }, { yAxis: 95, label: { formatter: 'B (95%)' } }], lineStyle: { type: 'dashed', color: '#6a6a8a' } }
        }
      ]
    });
  }
}
