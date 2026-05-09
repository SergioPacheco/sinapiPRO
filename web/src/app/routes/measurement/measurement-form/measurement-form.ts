import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';
import { MeasurementService } from '../services/measurement.service';

@Component({
  selector: 'app-measurement-form',
  templateUrl: './measurement-form.html',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, PageHeader],
})
export class MeasurementFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(MeasurementService);
  readonly router = inject(Router);

  form = this.fb.nonNullable.group({
    budgetId: ['', Validators.required],
    number: [1, [Validators.required, Validators.min(1)]],
    periodStart: ['', Validators.required],
    periodEnd: ['', Validators.required],
    retentionPct: [0.05, [Validators.min(0), Validators.max(1)]],
  });

  onSubmit() {
    if (this.form.invalid) return;
    this.service.create(this.form.getRawValue()).subscribe(() => this.router.navigate(['/measurements']));
  }
}
