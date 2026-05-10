import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { PageHeader } from '@shared';
import { ScheduleService } from '../services/schedule.service';
import { ScheduleActivity, SCurvePoint } from '../models/schedule.model';

@Component({
  selector: 'app-schedule-list',
  template: `
    <page-header title="Cronograma" subtitle="Gantt e Curva S do projeto">
      <button mat-stroked-button (click)="openReport()"><mat-icon>picture_as_pdf</mat-icon> PDF</button>
      <button mat-flat-button color="primary" (click)="create()"><mat-icon>add</mat-icon> Nova Atividade</button>
    </page-header>

    <!-- Gantt Chart -->
    <mat-card class="gantt-card">
      <div class="gantt-header">
        <h3>Diagrama de Gantt</h3>
        <div class="gantt-legend">
          <span><i class="bar-planned"></i> Planejado</span>
          <span><i class="bar-progress"></i> Progresso</span>
        </div>
      </div>
      <div class="gantt-container">
        @for (a of activities(); track a.id) {
          <div class="gantt-row">
            <div class="gantt-label" [matTooltip]="a.name">{{ a.name }}</div>
            <div class="gantt-bar-area">
              <div class="gantt-bar" [style.left.%]="barLeft(a)" [style.width.%]="barWidth(a)">
                <div class="gantt-progress" [style.width.%]="a.progressPct"></div>
              </div>
            </div>
            <div class="gantt-pct">{{ a.progressPct }}%</div>
          </div>
        }
        @if (activities().length === 0) {
          <div class="gantt-empty">Nenhuma atividade cadastrada</div>
        }
      </div>
    </mat-card>

    <!-- S-Curve -->
    @if (sCurvePoints().length > 0) {
      <mat-card class="curve-card">
        <h3>Curva S</h3>
        <svg viewBox="0 0 760 220" class="curve-chart">
          <line x1="40" y1="200" x2="720" y2="200" stroke="#ccc" />
          <line x1="40" y1="20" x2="40" y2="200" stroke="#ccc" />
          <polyline [attr.points]="plannedLine()" fill="none" stroke="#1565c0" stroke-width="3" />
          <polyline [attr.points]="actualLine()" fill="none" stroke="#4caf50" stroke-width="3" stroke-dasharray="6,3" />
        </svg>
        <div class="curve-legend">
          <span><i style="background:#1565c0"></i> Planejado</span>
          <span><i style="background:#4caf50"></i> Realizado</span>
        </div>
      </mat-card>
    }
  `,
  styles: `
    .gantt-card, .curve-card { margin-bottom: 16px; padding: 16px; }
    .gantt-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
    .gantt-header h3, .curve-card h3 { margin: 0; font-size: 16px; font-weight: 500; }
    .gantt-legend, .curve-legend { display: flex; gap: 16px; font-size: 12px; }
    .gantt-legend i, .curve-legend i { display: inline-block; width: 16px; height: 4px; border-radius: 2px; margin-right: 4px; vertical-align: middle; }
    .bar-planned { background: #e0e0e0; }
    .bar-progress { background: #4caf50; }
    .gantt-container { display: flex; flex-direction: column; gap: 4px; }
    .gantt-row { display: grid; grid-template-columns: 180px 1fr 50px; align-items: center; height: 32px; }
    .gantt-label { font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; padding-right: 8px; }
    .gantt-bar-area { position: relative; height: 20px; background: #f5f5f5; border-radius: 4px; }
    .gantt-bar { position: absolute; top: 2px; height: 16px; background: #e0e0e0; border-radius: 3px; min-width: 4px; transition: all 0.3s; }
    .gantt-progress { height: 100%; background: #4caf50; border-radius: 3px; transition: width 0.5s; }
    .gantt-pct { font-size: 12px; text-align: right; color: rgba(0,0,0,.6); }
    .gantt-empty { text-align: center; padding: 40px; color: rgba(0,0,0,.3); }
    .curve-chart { width: 100%; height: 220px; display: block; margin: 12px 0; }
    .curve-legend { display: flex; gap: 16px; font-size: 12px; margin-top: 8px; }
  `,
  imports: [MatButtonModule, MatCardModule, MatIconModule, MatTooltipModule, MatProgressBarModule, PageHeader],
})
export class ScheduleListComponent implements OnInit {
  private readonly service = inject(ScheduleService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private projectId = '';

  activities = signal<ScheduleActivity[]>([]);
  sCurvePoints = signal<SCurvePoint[]>([]);
  private projectStart = signal<Date>(new Date());
  private projectEnd = signal<Date>(new Date());

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.service.listActivities(this.projectId).subscribe(res => {
      this.activities.set(res);
      if (res.length > 0) {
        const starts = res.map(a => new Date(a.plannedStart).getTime());
        const ends = res.map(a => new Date(a.plannedEnd).getTime());
        this.projectStart.set(new Date(Math.min(...starts)));
        this.projectEnd.set(new Date(Math.max(...ends)));
      }
    });
    this.service.getSCurve(this.projectId).subscribe(d => this.sCurvePoints.set(d.points || []));
  }

  barLeft(a: ScheduleActivity): number {
    const total = this.projectEnd().getTime() - this.projectStart().getTime();
    if (total <= 0) return 0;
    return ((new Date(a.plannedStart).getTime() - this.projectStart().getTime()) / total) * 100;
  }

  barWidth(a: ScheduleActivity): number {
    const total = this.projectEnd().getTime() - this.projectStart().getTime();
    if (total <= 0) return 100;
    return ((new Date(a.plannedEnd).getTime() - new Date(a.plannedStart).getTime()) / total) * 100;
  }

  plannedLine(): string { return this.toPolyline(this.sCurvePoints().map(p => Number(p.plannedCumulative || 0))); }
  actualLine(): string { return this.toPolyline(this.sCurvePoints().map(p => Number(p.actualCumulative || 0))); }

  private toPolyline(values: number[]): string {
    if (!values.length) return '';
    const max = Math.max(1, ...values);
    const step = values.length === 1 ? 0 : 680 / (values.length - 1);
    return values.map((v, i) => `${40 + i * step},${200 - (v / max) * 180}`).join(' ');
  }

  openReport() { window.open(this.service.physicalFinancialReportUrl(this.projectId), '_blank'); }
  create() { this.router.navigate(['new'], { relativeTo: this.route }); }
}
