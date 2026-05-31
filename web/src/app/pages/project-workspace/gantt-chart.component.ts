import { Component, inject, OnInit, ElementRef, viewChild, Input } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as echarts from 'echarts';

interface ScheduleTask { id: string; name: string; startDate: string; endDate: string; progress: number; critical: boolean; dependencies: string[]; }

@Component({
  selector: 'app-gantt-chart',
  standalone: true,
  template: `
    <div class="gantt-header">
      <h3>Cronograma — Gantt</h3>
      <div class="legend">
        <span class="leg-item"><span class="dot critical"></span>Caminho Crítico</span>
        <span class="leg-item"><span class="dot normal"></span>Normal</span>
      </div>
    </div>
    <div #chart class="gantt-container"></div>
  `,
  styles: [`
    .gantt-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;
      h3 { margin: 0; color: #e0e0e0; font-size: 1rem; } }
    .legend { display: flex; gap: 1rem; font-size: 0.8rem; color: #8a8aaa; }
    .leg-item { display: flex; align-items: center; gap: 0.3rem; }
    .dot { width: 10px; height: 10px; border-radius: 2px; &.critical { background: #ef5350; } &.normal { background: #4fc3f7; } }
    .gantt-container { width: 100%; height: 500px; background: #16213e; border-radius: 10px; border: 1px solid #2a2a4a; }
  `]
})
export class GanttChartComponent implements OnInit {
  @Input() projectId = '';
  private http = inject(HttpClient);
  chartEl = viewChild<ElementRef>('chart');

  ngOnInit() {
    this.http.get<ScheduleTask[]>(`/projects/${this.projectId}/schedule/tasks`).subscribe(tasks => {
      setTimeout(() => this.render(tasks), 100);
    });
  }

  private render(tasks: ScheduleTask[]) {
    const el = this.chartEl()?.nativeElement;
    if (!el) return;
    const chart = echarts.init(el, 'dark');

    const categories = tasks.map(t => t.name);
    const startTime = Math.min(...tasks.map(t => new Date(t.startDate).getTime()));

    const data = tasks.map((t, i) => ({
      name: t.name,
      value: [i, new Date(t.startDate).getTime(), new Date(t.endDate).getTime(), t.progress],
      itemStyle: { color: t.critical ? '#ef5350' : '#4fc3f7', opacity: 0.8 }
    }));

    // Progress overlay
    const progressData = tasks.map((t, i) => {
      const start = new Date(t.startDate).getTime();
      const end = new Date(t.endDate).getTime();
      const progressEnd = start + (end - start) * (t.progress / 100);
      return { value: [i, start, progressEnd, t.progress], itemStyle: { color: t.critical ? '#b71c1c' : '#0277bd' } };
    });

    chart.setOption({
      tooltip: { formatter: (p: any) => `${p.name}<br/>Progresso: ${p.value[3]}%` },
      grid: { left: 200, right: 30, top: 30, bottom: 30 },
      xAxis: { type: 'time', axisLabel: { color: '#8a8aaa' } },
      yAxis: { type: 'category', data: categories, inverse: true, axisLabel: { color: '#b0b0b0', fontSize: 11 } },
      series: [
        { type: 'custom', renderItem: (params: any, api: any) => this.renderBar(params, api), encode: { x: [1, 2], y: 0 }, data },
        { type: 'custom', renderItem: (params: any, api: any) => this.renderBar(params, api), encode: { x: [1, 2], y: 0 }, data: progressData }
      ]
    });
  }

  private renderBar(params: any, api: any) {
    const catIdx = api.value(0);
    const start = api.coord([api.value(1), catIdx]);
    const end = api.coord([api.value(2), catIdx]);
    const height = api.size([0, 1])[1] * 0.5;
    return { type: 'rect', shape: { x: start[0], y: start[1] - height / 2, width: end[0] - start[0], height }, style: api.style() };
  }
}
