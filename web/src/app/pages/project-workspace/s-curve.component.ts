import { Component, input, OnInit, OnDestroy, ElementRef, viewChild, afterNextRender } from '@angular/core';
import * as echarts from 'echarts';

export interface SCurveData {
  periods: string[];      // ex: ['Jan', 'Fev', 'Mar', ...]
  planned: number[];      // % acumulado planejado
  actual: number[];       // % acumulado realizado
}

@Component({
  selector: 'app-s-curve',
  standalone: true,
  template: `<div #chart class="s-curve-chart"></div>`,
  styles: [`.s-curve-chart { width: 100%; height: 320px; }`],
})
export class SCurveComponent implements OnInit, OnDestroy {
  data = input.required<SCurveData>();

  private chartEl = viewChild.required<ElementRef>('chart');
  private chart: echarts.ECharts | null = null;

  constructor() {
    afterNextRender(() => this.renderChart());
  }

  ngOnInit() {}

  ngOnDestroy() {
    this.chart?.dispose();
  }

  private renderChart() {
    const el = this.chartEl().nativeElement;
    this.chart = echarts.init(el, 'dark');

    const d = this.data();
    this.chart.setOption({
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'axis',
        formatter: (params: any) =>
          params.map((p: any) => `${p.marker} ${p.seriesName}: ${p.value.toFixed(1)}%`).join('<br>'),
      },
      legend: {
        data: ['Planejado', 'Realizado'],
        textStyle: { color: '#9ca3af' },
        bottom: 0,
      },
      grid: { top: 30, right: 20, bottom: 50, left: 50 },
      xAxis: {
        type: 'category',
        data: d.periods,
        axisLine: { lineStyle: { color: '#333840' } },
        axisLabel: { color: '#9ca3af', fontSize: 11 },
      },
      yAxis: {
        type: 'value',
        max: 100,
        axisLabel: { color: '#9ca3af', formatter: '{value}%' },
        splitLine: { lineStyle: { color: '#333840', type: 'dashed' } },
      },
      series: [
        {
          name: 'Planejado',
          type: 'line',
          smooth: true,
          data: d.planned,
          lineStyle: { width: 2, color: '#3b82f6' },
          itemStyle: { color: '#3b82f6' },
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59, 130, 246, 0.15)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0)' },
          ])},
        },
        {
          name: 'Realizado',
          type: 'line',
          smooth: true,
          data: d.actual,
          lineStyle: { width: 2, color: '#10b981' },
          itemStyle: { color: '#10b981' },
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16, 185, 129, 0.15)' },
            { offset: 1, color: 'rgba(16, 185, 129, 0)' },
          ])},
        },
      ],
    });

    // Resize observer para responsividade
    const observer = new ResizeObserver(() => this.chart?.resize());
    observer.observe(el);
  }
}
