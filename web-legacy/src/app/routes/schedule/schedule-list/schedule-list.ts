import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { PageHeader } from '@shared';
import { ScheduleService } from '../services/schedule.service';
import { GanttChartComponent } from '../gantt-chart/gantt-chart';
import { Holiday, ScheduleActivity, SCurvePoint, ScheduleBaseline, ScheduleTrackingLine } from '../models/schedule.model';

@Component({
  selector: 'app-schedule-list',
  template: `
    <page-header title="Planejamento" subtitle="Cronograma, feriados, baselines e acompanhamento">
      <button mat-stroked-button (click)="openReport()"><mat-icon>picture_as_pdf</mat-icon> PDF</button>
      <button mat-flat-button color="primary" (click)="create()"><mat-icon>add</mat-icon> Nova Atividade</button>
    </page-header>

    <div class="toolbar">
      <mat-card class="toolbar-card">
        <mat-form-field appearance="outline">
          <mat-label>Início do replanejamento</mat-label>
          <input matInput type="date" [(ngModel)]="distributionStart" />
        </mat-form-field>
        <button mat-stroked-button color="primary" (click)="distributeDates()">
          <mat-icon>timeline</mat-icon>
          Distribuir datas
        </button>
      </mat-card>
      <mat-card class="toolbar-card">
        <mat-form-field appearance="outline">
          <mat-label>Baseline</mat-label>
          <input matInput [(ngModel)]="baselineName" placeholder="Ex: Baseline inicial" />
        </mat-form-field>
        <button mat-stroked-button color="primary" (click)="saveBaseline()">
          <mat-icon>bookmark</mat-icon>
          Salvar baseline
        </button>
      </mat-card>
    </div>

    <div class="grid">
      <mat-card class="panel">
        <div class="panel-header">
          <h3>Gantt</h3>
          <span>{{ activities().length }} atividades</span>
        </div>
        <app-gantt-chart [projectId]="projectId" />
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
            <div class="empty">Nenhuma atividade cadastrada</div>
          }
        </div>
      </mat-card>

      <mat-card class="panel">
        <div class="panel-header">
          <h3>Acompanhamento</h3>
          <span>{{ tracking().length }} linhas</span>
        </div>
        <div class="tracking-list">
          @for (line of tracking(); track line.activityId) {
            <div class="tracking-row" [class.delayed]="line.status === 'DELAYED'">
              <strong>{{ line.name }}</strong>
              <span>{{ line.plannedStart }} → {{ line.plannedEnd }}</span>
              <span>{{ line.progressPct | number:'1.1-1' }}%</span>
              <span>{{ line.status === 'ON_TRACK' ? 'No prazo' : 'Atrasado' }}</span>
            </div>
          }
          @if (tracking().length === 0) {
            <div class="empty">Sem acompanhamento disponível</div>
          }
        </div>
      </mat-card>
    </div>

    <div class="grid">
      <mat-card class="panel">
        <div class="panel-header">
          <h3>Curva S</h3>
        </div>
        @if (sCurvePoints().length > 0) {
          <svg viewBox="0 0 760 220" class="curve-chart">
            <line x1="40" y1="200" x2="720" y2="200" stroke="#ccc" />
            <line x1="40" y1="20" x2="40" y2="200" stroke="#ccc" />
            <polyline [attr.points]="plannedLine()" fill="none" stroke="#1565c0" stroke-width="3" />
            <polyline [attr.points]="actualLine()" fill="none" stroke="#4caf50" stroke-width="3" stroke-dasharray="6,3" />
          </svg>
        } @else {
          <div class="empty">Sem dados da Curva S</div>
        }
      </mat-card>

      <mat-card class="panel">
        <div class="panel-header">
          <h3>Feriados</h3>
        </div>
        <div class="holiday-form">
          <mat-form-field appearance="outline">
            <mat-label>Data</mat-label>
            <input matInput type="date" [(ngModel)]="holidayForm.date" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Descrição</mat-label>
            <input matInput [(ngModel)]="holidayForm.description" />
          </mat-form-field>
          <mat-checkbox [(ngModel)]="holidayForm.recurring">Recorrente</mat-checkbox>
          <button mat-stroked-button color="primary" (click)="addHoliday()">
            <mat-icon>event</mat-icon>
            Adicionar feriado
          </button>
        </div>
        <div class="holiday-list">
          @for (h of holidays(); track h.id) {
            <div class="holiday-row">
              <strong>{{ h.holidayDate }}</strong>
              <span>{{ h.description || 'Feriado' }}</span>
              <span>{{ h.recurring ? 'Recorrente' : 'Único' }}</span>
            </div>
          }
          @if (holidays().length === 0) {
            <div class="empty">Nenhum feriado cadastrado</div>
          }
        </div>
      </mat-card>
    </div>

    <mat-card class="panel">
      <div class="panel-header">
        <h3>Baselines</h3>
      </div>
      <div class="baseline-list">
        @for (b of baselines(); track b.id) {
          <div class="baseline-row">
            <strong>{{ b.name }}</strong>
            <span>{{ b.activityCount }} atividades</span>
            <span>{{ b.createdAt }}</span>
          </div>
        }
        @if (baselines().length === 0) {
          <div class="empty">Nenhuma baseline salva</div>
        }
      </div>
    </mat-card>
  `,
  styles: `
    .toolbar { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 16px; }
    .toolbar-card { display: flex; gap: 12px; align-items: center; padding: 12px; }
    .toolbar-card mat-form-field { flex: 1; }
    .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
    .panel { padding: 16px; }
    .panel-header { display:flex; justify-content:space-between; align-items:center; margin-bottom: 12px; }
    .panel-header h3 { margin: 0; }
    .gantt-container { display: flex; flex-direction: column; gap: 4px; }
    .gantt-row { display: grid; grid-template-columns: 180px 1fr 50px; align-items: center; height: 32px; }
    .gantt-label { font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; padding-right: 8px; }
    .gantt-bar-area { position: relative; height: 20px; background: #f5f5f5; border-radius: 4px; }
    .gantt-bar { position: absolute; top: 2px; height: 16px; background: #e0e0e0; border-radius: 3px; min-width: 4px; }
    .gantt-progress { height: 100%; background: #4caf50; border-radius: 3px; }
    .gantt-pct { font-size: 12px; text-align: right; color: var(--mat-sys-on-surface-variant); }
    .tracking-list, .holiday-list, .baseline-list { display: grid; gap: 8px; }
    .tracking-row, .holiday-row, .baseline-row { display:grid; grid-template-columns: 1.5fr 1fr auto auto; gap: 12px; align-items: center; padding: 10px 12px; border-radius: 10px; background: var(--mat-sys-surface-container-low); }
    .tracking-row.delayed { border-left: 4px solid #d32f2f; }
    .holiday-form { display:grid; gap: 12px; margin-bottom: 12px; }
    .curve-chart { width: 100%; height: 220px; display: block; margin: 12px 0; }
    .empty { text-align:center; padding: 28px; color: var(--mat-sys-on-surface-variant); }
    @media (max-width: 1200px) { .toolbar, .grid { grid-template-columns: 1fr; } }
  `,
  imports: [FormsModule, DecimalPipe, MatButtonModule, MatCardModule, MatIconModule, MatTooltipModule, MatProgressBarModule, MatFormFieldModule, MatInputModule, MatCheckboxModule, PageHeader, GanttChartComponent],
})
export class ScheduleListComponent implements OnInit {
  private readonly service = inject(ScheduleService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  projectId = '';

  activities = signal<ScheduleActivity[]>([]);
  sCurvePoints = signal<SCurvePoint[]>([]);
  tracking = signal<ScheduleTrackingLine[]>([]);
  holidays = signal<Holiday[]>([]);
  baselines = signal<ScheduleBaseline[]>([]);
  baselineName = '';
  distributionStart = new Date().toISOString().slice(0, 10);
  holidayForm = { date: '', description: '', recurring: false };
  private projectStart = signal<Date>(new Date());
  private projectEnd = signal<Date>(new Date());

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.loadAll();
  }

  loadAll() {
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
    this.service.getTracking(this.projectId).subscribe(t => this.tracking.set(t));
    this.service.listHolidays(this.projectId).subscribe(h => this.holidays.set(h));
    this.service.listBaselines(this.projectId).subscribe(b => this.baselines.set(b));
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

  distributeDates() {
    if (!this.distributionStart) return;
    this.service.distributeDates(this.projectId, this.distributionStart).subscribe(() => this.loadAll());
  }

  saveBaseline() {
    if (!this.baselineName.trim()) return;
    this.service.createBaseline(this.projectId, this.baselineName.trim()).subscribe(() => {
      this.baselineName = '';
      this.loadAll();
    });
  }

  addHoliday() {
    if (!this.holidayForm.date) return;
    this.service.addHoliday(this.projectId, {
      date: this.holidayForm.date,
      description: this.holidayForm.description.trim(),
      recurring: this.holidayForm.recurring,
    }).subscribe(() => {
      this.holidayForm = { date: '', description: '', recurring: false };
      this.loadAll();
    });
  }
}
