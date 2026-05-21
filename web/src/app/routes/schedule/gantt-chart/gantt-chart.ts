import { Component, inject, Input, OnInit, OnChanges, signal, computed } from '@angular/core';
import { CdkDrag, CdkDragEnd } from '@angular/cdk/drag-drop';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { DatePipe } from '@angular/common';
import { ScheduleService, GanttActivity, GanttDependency, GanttData } from '../services/schedule.service';

@Component({
  selector: 'app-gantt-chart',
  standalone: true,
  imports: [CdkDrag, MatTooltipModule, MatProgressBarModule, DatePipe],
  template: `
    <div class="gantt-container">
      <div class="gantt-header">
        <div class="gantt-label-col">Atividade</div>
        <div class="gantt-timeline-col">
          @for (month of months(); track month) {
            <div class="gantt-month" [style.width.px]="monthWidth">{{ month }}</div>
          }
        </div>
      </div>

      <div class="gantt-body" #ganttBody>
        <!-- SVG overlay for dependency lines -->
        <svg class="gantt-svg-overlay" [attr.width]="svgWidth()" [attr.height]="svgHeight()">
          @for (line of dependencyLines(); track $index) {
            <path [attr.d]="line.path" fill="none"
                  [attr.stroke]="line.critical ? '#d32f2f' : '#90a4ae'"
                  [attr.stroke-width]="line.critical ? 2.5 : 1.5"
                  stroke-dasharray="{{line.critical ? '0' : '4,3'}}"
                  marker-end="url(#arrowhead)" />
          }
          <defs>
            <marker id="arrowhead" markerWidth="8" markerHeight="6" refX="8" refY="3" orient="auto">
              <polygon points="0 0, 8 3, 0 6" fill="#90a4ae" />
            </marker>
          </defs>
        </svg>

        @for (activity of activities(); track activity.id; let i = $index) {
          <div class="gantt-row" [class.critical]="isCritical(activity.id)">
            <div class="gantt-label-col" [matTooltip]="activity.name">
              {{ activity.name | slice:0:25 }}
            </div>
            <div class="gantt-timeline-col">
              <div class="gantt-bar-wrapper"
                   cdkDrag
                   cdkDragLockAxis="x"
                   [cdkDragFreeDragPosition]="{x: barLeft(activity), y: 0}"
                   (cdkDragEnded)="onDragEnd($event, activity)"
                   [style.left.px]="barLeft(activity)"
                   [style.width.px]="barWidth(activity)"
                   [class.critical-bar]="isCritical(activity.id)"
                   [matTooltip]="activity.plannedStart + ' → ' + activity.plannedEnd + ' (' + activity.progressPct + '%)'">
                <div class="gantt-bar">
                  <div class="gantt-bar-progress" [style.width.%]="activity.progressPct"></div>
                </div>
              </div>
            </div>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    .gantt-container { overflow-x: auto; border: 1px solid var(--mat-sys-outline-variant); border-radius: 8px; }
    .gantt-header { display: flex; border-bottom: 1px solid var(--mat-sys-outline-variant); background: var(--mat-sys-surface-container); }
    .gantt-body { min-height: 200px; position: relative; }
    .gantt-svg-overlay { position: absolute; top: 0; left: 200px; pointer-events: none; z-index: 2; }
    .gantt-row { display: flex; border-bottom: 1px solid var(--mat-sys-outline-variant); height: 36px; align-items: center; }
    .gantt-row:hover { background: var(--mat-sys-surface-container-low); }
    .gantt-row.critical { background: rgba(211, 47, 47, 0.04); }
    .gantt-label-col { width: 200px; min-width: 200px; padding: 0 12px; font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .gantt-timeline-col { flex: 1; display: flex; position: relative; min-width: 600px; }
    .gantt-month { text-align: center; font-size: 11px; border-right: 1px solid var(--mat-sys-outline-variant); padding: 4px 0; }
    .gantt-bar-wrapper { position: absolute; height: 20px; cursor: grab; z-index: 3; }
    .gantt-bar-wrapper:active { cursor: grabbing; }
    .gantt-bar { height: 100%; background: var(--mat-sys-primary); border-radius: 4px; position: relative; overflow: hidden; }
    .gantt-bar-progress { height: 100%; background: var(--mat-sys-primary-container); opacity: 0.6; }
    .critical-bar .gantt-bar { background: #d32f2f; }
    .critical-bar .gantt-bar-progress { background: #ef9a9a; }
  `]
})
export class GanttChartComponent implements OnInit, OnChanges {
  @Input() projectId = '';
  @Input() criticalPath: string[] = [];

  private readonly scheduleService = inject(ScheduleService);

  activities = signal<GanttActivity[]>([]);
  dependencies = signal<GanttDependency[]>([]);

  readonly monthWidth = 80;
  private projectStart = signal<Date>(new Date());
  private projectEnd = signal<Date>(new Date());
  private readonly ROW_HEIGHT = 36;

  months = computed(() => {
    const start = this.projectStart();
    const end = this.projectEnd();
    const result: string[] = [];
    const cursor = new Date(start.getFullYear(), start.getMonth(), 1);
    while (cursor <= end) {
      result.push(cursor.toLocaleDateString('pt-BR', { month: 'short', year: '2-digit' }));
      cursor.setMonth(cursor.getMonth() + 1);
    }
    return result.length > 0 ? result : [''];
  });

  svgWidth = computed(() => this.months().length * this.monthWidth);
  svgHeight = computed(() => this.activities().length * this.ROW_HEIGHT);

  dependencyLines = computed(() => {
    const acts = this.activities();
    const deps = this.dependencies();
    if (acts.length === 0) return [];

    return deps.map(dep => {
      const predIdx = acts.findIndex(a => a.id === dep.predecessorId);
      const succIdx = acts.findIndex(a => a.id === dep.successorId);
      if (predIdx < 0 || succIdx < 0) return null;

      const pred = acts[predIdx];
      const succ = acts[succIdx];
      const x1 = this.barLeft(pred) + this.barWidth(pred);
      const y1 = predIdx * this.ROW_HEIGHT + this.ROW_HEIGHT / 2;
      const x2 = this.barLeft(succ);
      const y2 = succIdx * this.ROW_HEIGHT + this.ROW_HEIGHT / 2;

      const midX = x1 + (x2 - x1) / 2;
      const path = `M ${x1} ${y1} C ${midX} ${y1}, ${midX} ${y2}, ${x2} ${y2}`;
      const critical = this.isCritical(dep.predecessorId) && this.isCritical(dep.successorId);
      return { path, critical };
    }).filter(Boolean) as { path: string; critical: boolean }[];
  });

  ngOnInit() { this.loadData(); }
  ngOnChanges() { this.loadData(); }

  private loadData() {
    if (!this.projectId) return;
    this.scheduleService.getGanttData(this.projectId).subscribe(data => {
      this.activities.set(data.activities);
      this.dependencies.set(data.dependencies);
      this.calculateBounds(data.activities);
    });
    if (this.criticalPath.length === 0) {
      this.scheduleService.getCriticalPath(this.projectId).subscribe((result: any) => {
        if (result?.criticalActivityIds) {
          this.criticalPath = result.criticalActivityIds;
        }
      });
    }
  }

  private calculateBounds(activities: GanttActivity[]) {
    if (activities.length === 0) return;
    const starts = activities.map(a => new Date(a.plannedStart).getTime());
    const ends = activities.map(a => new Date(a.plannedEnd).getTime());
    this.projectStart.set(new Date(Math.min(...starts)));
    this.projectEnd.set(new Date(Math.max(...ends)));
  }

  isCritical(activityId: string): boolean {
    return this.criticalPath.includes(activityId);
  }

  barLeft(activity: GanttActivity): number {
    const start = new Date(activity.plannedStart).getTime();
    const projStart = this.projectStart().getTime();
    const projEnd = this.projectEnd().getTime();
    const totalWidth = this.months().length * this.monthWidth;
    const range = projEnd - projStart || 1;
    return ((start - projStart) / range) * totalWidth;
  }

  barWidth(activity: GanttActivity): number {
    const start = new Date(activity.plannedStart).getTime();
    const end = new Date(activity.plannedEnd).getTime();
    const projStart = this.projectStart().getTime();
    const projEnd = this.projectEnd().getTime();
    const totalWidth = this.months().length * this.monthWidth;
    const range = projEnd - projStart || 1;
    return Math.max(((end - start) / range) * totalWidth, 8);
  }

  onDragEnd(event: CdkDragEnd, activity: GanttActivity) {
    const deltaX = event.distance.x;
    const totalWidth = this.months().length * this.monthWidth;
    const projStart = this.projectStart().getTime();
    const projEnd = this.projectEnd().getTime();
    const range = projEnd - projStart || 1;
    const deltaDays = Math.round((deltaX / totalWidth) * (range / (1000 * 60 * 60 * 24)));

    const newStart = new Date(activity.plannedStart);
    const newEnd = new Date(activity.plannedEnd);
    newStart.setDate(newStart.getDate() + deltaDays);
    newEnd.setDate(newEnd.getDate() + deltaDays);

    const startStr = newStart.toISOString().split('T')[0];
    const endStr = newEnd.toISOString().split('T')[0];

    this.scheduleService.updateActivityDates(this.projectId, activity.id, startStr, endStr)
      .subscribe(updated => {
        this.activities.update(acts =>
          acts.map(a => a.id === updated.id ? { ...a, plannedStart: updated.plannedStart, plannedEnd: updated.plannedEnd } : a)
        );
        event.source._dragRef.reset();
      });
  }
}
