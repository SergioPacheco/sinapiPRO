import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-safety-form',
  templateUrl: './safety-form.html',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, PageHeader],
})
export class SafetyFormComponent {
  private readonly fb = inject(FormBuilder);
  readonly router = inject(Router);

  form = this.fb.nonNullable.group({
    date: ['', Validators.required],
    description: ['', Validators.required],
    severity: ['LOW' as string, Validators.required],
    projectId: [''],
  });

  onSubmit() {
    if (this.form.invalid) return;
    this.router.navigate(['/safety']);
  }
}
