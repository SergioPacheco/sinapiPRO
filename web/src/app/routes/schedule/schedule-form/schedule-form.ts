import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-schedule-form',
  templateUrl: './schedule-form.html',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, PageHeader],
})
export class ScheduleFormComponent {
  private readonly fb = inject(FormBuilder);
  readonly router = inject(Router);

  form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    progress: [0, [Validators.min(0), Validators.max(100)]],
  });

  get duration(): number {
    const start = this.form.value.startDate;
    const end = this.form.value.endDate;
    if (start && end) {
      const diff = new Date(end).getTime() - new Date(start).getTime();
      return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)));
    }
    return 0;
  }

  onSubmit() {
    if (this.form.invalid) return;
    this.router.navigate(['/schedule']);
  }
}
