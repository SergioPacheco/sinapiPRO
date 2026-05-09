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
  readonly router = inject(Router);

  form = this.fb.nonNullable.group({
    date: ['', Validators.required],
    weather: ['SUNNY' as string, Validators.required],
    temperature: [25, Validators.required],
    laborCount: [0, Validators.min(0)],
    equipmentCount: [0, Validators.min(0)],
    notes: [''],
  });

  weathers = ['SUNNY', 'CLOUDY', 'RAINY', 'STORMY'];

  onSubmit() {
    if (this.form.invalid) return;
    this.service.create(this.form.getRawValue()).subscribe(() => this.router.navigate(['/daily-log']));
  }
}
