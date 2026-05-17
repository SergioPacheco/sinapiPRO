import { ActivatedRoute } from '@angular/router';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';
import { DailyLogService } from '../services/daily-log.service';

@Component({
  selector: 'app-daily-log-form',
  templateUrl: './daily-log-form.html',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, PageHeader],
})
export class DailyLogFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(DailyLogService);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';
  readonly router = inject(Router);

  form = this.fb.nonNullable.group({
    logDate: ['', Validators.required],
    weatherMorning: ['SUNNY' as string],
    weatherAfternoon: ['SUNNY' as string],
    observations: [''],
    weatherHoursLost: [0],
    weatherCondition: [''],
    weatherImpact: [''],
  });

  weathers = ['SUNNY', 'CLOUDY', 'RAINY', 'STORMY'];

  onSubmit() {
    if (this.form.invalid) return;
    this.projectId = this.findProjectId();
    const value = this.form.getRawValue();
    this.service.create(this.projectId, {
      logDate: value.logDate,
      weatherMorning: value.weatherMorning,
      weatherAfternoon: value.weatherAfternoon,
      observations: value.observations,
      labor: [],
      equipment: [],
      occurrences: [],
    }).subscribe(() => {
      if ((value.weatherHoursLost || 0) > 0 && value.weatherCondition) {
        this.service.recordWeatherDelay(this.projectId, {
          delayDate: value.logDate,
          weatherCondition: value.weatherCondition,
          hoursLost: value.weatherHoursLost,
          impactDescription: value.weatherImpact,
        }).subscribe(() => this.router.navigate(['../'], { relativeTo: this.route }));
        return;
      }
      this.router.navigate(['../'], { relativeTo: this.route });
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
