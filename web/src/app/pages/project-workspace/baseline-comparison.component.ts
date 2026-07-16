import { Component, inject, OnInit, OnDestroy, signal, computed, ElementRef, viewChild, afterNextRender } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe, DatePipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import * as echarts from 'echarts';

interface BaselineEntry {
  activityId: string;
  name: string;
  baselineStart: string;
  baselineEnd: string;
  currentStart: string;
  currentEnd: string;
  baselineDuration: number;
  currentDuration: number;
  delayDays: number;
  status: 'ON_TRACK' | 'DELAYED' | 'AHEAD';
}

interface BaselineSummary {
  totalActivities: number;
  onTrack: number;
  delayed: number;
  ahead: number;
  avgDelay: number;
  entries: BaselineEntry[];
}

@Component({
  selector: 'app-baseline-comparison',
  standalone: true,
  imports: [DecimalPipe, DatePipe, TableModule, TagModule, ButtonModule],
  template: `
    <div class="baseline-container">
      <div class="baseline-header">
        <h3 style="margin:0;color:var(--sp-text)">Comparação com Baseline</h3>
        <div class="flex gap-3">
          <div class="metric"><span class="label">No prazo</span><span class="value ok">{{ summary().onTrack }}</span></div>
          <div class="metric"><span class="label">Atrasadas</span><span class="value danger">{{ summary().delayed }}</span></div>
          <div class="metric"><span class="label">Adiantadas</span><span class="value ahead">{{ summary().ahead }}</span></div>
          <div class="metric"><span class="label">Atraso médio</span><span class="value">{{ summary().avgDelay | number:'1.0-0' }}d</span></div>
        </div>
      </div>

      <!-- Variance Chart -->
      <div #chart class="variance-chart"></div>

      <!-- Detail Table -->
      <p-table [value]="entries()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="entries().length > 15" [rows]="15">
        <ng-template pTemplate="header">
          <tr>
            <th>Atividade</th>
            <th style="width:100px">Baseline Início</th>
            <th style="width:100px">Baseline Fim</th>
            <th style="width:100px">Atual Início</th>
            <th style="width:100px">Atual Fim</th>
            <th style="width:80px">Variação</th>
            <th style="width:90px">Status</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-e>
          <tr>
            <td>{{ e.name }}</td>
            <td style="font-size:11px">{{ e.baselineStart }}</td>
            <td style="font-size:11px">{{ e.baselineEnd }}</td>
            <td style="font-size:11px">{{ e.currentStart }}</td>
            <td style="font-size:11px">{{ e.currentEnd }}</td>
            <td class="text-center" [style.color]="e.delayDays > 0 ? 'var(--sp-danger)' : e.delayDays < 0 ? 'var(--sp-success)' : 'var(--sp-text-muted)'">
              {{ e.delayDays > 0 ? '+' : '' }}{{ e.delayDays }}d
            </td>
            <td>
              @switch (e.status) {
                @case ('ON_TRACK') { <p-tag value="No prazo" severity="success" [rounded]="true" /> }
                @case ('DELAYED') { <p-tag value="Atrasada" severity="danger" [rounded]="true" /> }
                @case ('AHEAD') { <p-tag value="Adiantada" severity="info" [rounded]="true" /> }
              }
            </td>
          </tr>
        </ng-template>
        <ng-template pTemplate="emptymessage">
          <tr><td colspan="7" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhum baseline salvo para esta obra</td></tr>
        </ng-template>
      </p-table>
    </div>
  `,
  styles: [`
    .baseline-container { display: flex; flex-direction: column; gap: 1rem; }
    .baseline-header { display: flex; align-items: center; justify-content: space-between; }
    .metric { display: flex; flex-direction: column; align-items: center; gap: 2px; }
    .metric .label { font-size: 10px; color: var(--sp-text-muted); text-transform: uppercase; }
    .metric .value { font-size: 18px; font-weight: 700; color: var(--sp-text); }
    .metric .value.ok { color: var(--sp-success); }
    .metric .value.danger { color: var(--sp-danger); }
    .metric .value.ahead { color: var(--sp-primary); }
    .variance-chart { width: 100%; height: 200px; }
  `],
})
export class BaselineComparisonComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private chartEl = viewChild<ElementRef>('chart');
  private chart: echarts.ECharts | null = null;

  summary = signal<BaselineSummary>({ totalActivities: 0, onTrack: 0, delayed: 0, ahead: 0, avgDelay: 0, entries: [] });
  entries = computed(() => this.summary().entries);

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  constructor() {
    afterNextRender(() => {
      if (this.entries().length > 0) this.renderChart();
    });
  }

  ngOnInit() {
    this.http.get<BaselineSummary>(`/projects/${this.pid}/schedule/baseline-comparison`).subscribe({
      next: data => {
        this.summary.set(data);
        setTimeout(() => this.renderChart(), 50);
      },
    });
  }

  ngOnDestroy() {
    this.chart?.dispose();
  }

  private renderChart() {
    const el = this.chartEl()?.nativeElement;
    if (!el || this.entries().length === 0) return;

    this.chart = echarts.init(el, 'dark');
    const entries = this.entries();

    this.chart.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', formatter: (p: any) => `${p[0].name}: ${p[0].value > 0 ? '+' : ''}${p[0].value} dias` },
      grid: { top: 20, right: 20, bottom: 30, left: 40 },
      xAxis: {
        type: 'category',
        data: entries.map(e => e.name.length > 15 ? e.name.slice(0, 15) + '…' : e.name),
        axisLabel: { color: '#9ca3af', fontSize: 10, rotate: 30 },
        axisLine: { lineStyle: { color: '#333840' } },
      },
      yAxis: {
        type: 'value',
        name: 'Dias',
        axisLabel: { color: '#9ca3af' },
        splitLine: { lineStyle: { color: '#333840', type: 'dashed' } },
      },
      series: [{
        type: 'bar',
        data: entries.map(e => ({
          value: e.delayDays,
          itemStyle: { color: e.delayDays > 0 ? '#ef4444' : e.delayDays < 0 ? '#10b981' : '#6b7280' },
        })),
        barWidth: '60%',
      }],
    });

    new ResizeObserver(() => this.chart?.resize()).observe(el);
  }
}
