import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';
import { NextActionService } from '@shared';
import { ScheduleService } from '../services/schedule.service';

@Component({
  selector: 'app-schedule-form',
  templateUrl: './schedule-form.html',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, PageHeader],
})
export class ScheduleFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(ScheduleService);
  private readonly route = inject(ActivatedRoute);
  private readonly nextAction = inject(NextActionService);
  readonly router = inject(Router);

  form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    plannedStart: ['', Validators.required],
    plannedEnd: ['', Validators.required],
    weightPct: [0, [Validators.required, Validators.min(0.01), Validators.max(100)]],
    sortOrder: [1, [Validators.required, Validators.min(1)]],
  });

  get duration(): number {
    const start = this.form.value.plannedStart;
    const end = this.form.value.plannedEnd;
    if (start && end) {
      const diff = new Date(end).getTime() - new Date(start).getTime();
      return Math.max(1, Math.ceil(diff / (1000 * 60 * 60 * 24)) + 1);
    }
    return 0;
  }

  onSubmit() {
    if (this.form.invalid) return;
    const projectId = this.findProjectId();
    const value = this.form.getRawValue();
    this.service.createActivity(projectId, {
      name: value.name,
      plannedStart: value.plannedStart,
      plannedEnd: value.plannedEnd,
      weight: Number((value.weightPct / 100).toFixed(6)),
      sortOrder: value.sortOrder,
    }).subscribe(() => {
      this.router.navigate(['../'], { relativeTo: this.route });
      this.nextAction.suggest('schedule.created');
    });
  }

  cancel() {
    this.router.navigate(['../'], { relativeTo: this.route });
  }

  private findProjectId() {
    let route = this.route.snapshot;
    while (route.parent && !route.paramMap.get('projectId')) route = route.parent;
    return route.paramMap.get('projectId') || '';
  }
}
