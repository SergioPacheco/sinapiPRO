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

      <div class="gantt-body">
        @for (activity of activities(); track activity.id) {
          <div class="gantt-row">
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
                   [matTooltip]="activity.plannedStart + ' → ' + activity.plannedEnd">
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
    .gantt-body { min-height: 200px; }
    .gantt-row { display: flex; border-bottom: 1px solid var(--mat-sys-outline-variant); height: 36px; align-items: center; }
    .gantt-row:hover { background: var(--mat-sys-surface-container-low); }
    .gantt-label-col { width: 200px; min-width: 200px; padding: 0 12px; font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .gantt-timeline-col { flex: 1; display: flex; position: relative; min-width: 600px; }
    .gantt-month { text-align: center; font-size: 11px; border-right: 1px solid var(--mat-sys-outline-variant); padding: 4px 0; }
    .gantt-bar-wrapper { position: absolute; height: 20px; cursor: grab; z-index: 1; }
    .gantt-bar-wrapper:active { cursor: grabbing; }
    .gantt-bar { height: 100%; background: var(--mat-sys-primary); border-radius: 4px; position: relative; overflow: hidden; }
    .gantt-bar-progress { height: 100%; background: var(--mat-sys-primary-container); opacity: 0.6; }
  `]
})
export class GanttChartComponent implements OnInit, OnChanges {
  @Input() projectId = '';

  private readonly scheduleService = inject(ScheduleService);

  activities = signal<GanttActivity[]>([]);
  dependencies = signal<GanttDependency[]>([]);

  readonly monthWidth = 80;
  private projectStart = signal<Date>(new Date());
  private projectEnd = signal<Date>(new Date());

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

  ngOnInit() { this.loadData(); }
  ngOnChanges() { this.loadData(); }

  private loadData() {
    if (!this.projectId) return;
    this.scheduleService.getGanttData(this.projectId).subscribe(data => {
      this.activities.set(data.activities);
      this.dependencies.set(data.dependencies);
      this.calculateBounds(data.activities);
    });
  }

  private calculateBounds(activities: GanttActivity[]) {
    if (activities.length === 0) return;
    const starts = activities.map(a => new Date(a.plannedStart).getTime());
    const ends = activities.map(a => new Date(a.plannedEnd).getTime());
    this.projectStart.set(new Date(Math.min(...starts)));
    this.projectEnd.set(new Date(Math.max(...ends)));
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
