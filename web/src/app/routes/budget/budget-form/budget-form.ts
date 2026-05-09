import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';
import { BudgetService } from '../services/budget.service';

@Component({
  selector: 'app-budget-form',
  templateUrl: './budget-form.html',
  imports: [
    ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatDatepickerModule, MatButtonModule, PageHeader,
  ],
})
export class BudgetFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly budgetService = inject(BudgetService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  isEdit = false;
  private budgetId = '';

  form = this.fb.nonNullable.group({
    code: ['', [Validators.required, Validators.maxLength(40)]],
    title: ['', [Validators.required, Validators.maxLength(140)]],
    customerName: ['', [Validators.required, Validators.maxLength(140)]],
    totalAmount: [0, [Validators.required, Validators.min(0)]],
    status: ['ESTIMATE' as string, Validators.required],
    startDate: ['', Validators.required],
    endDate: [''],
  });

  statuses = [
    { value: 'ESTIMATE', label: 'Estimativa' },
    { value: 'SALE', label: 'Venda' },
    { value: 'EXECUTION', label: 'Execução' },
    { value: 'COMPLETED', label: 'Concluído' },
  ];

  ngOnInit() {
    this.budgetId = this.route.snapshot.params['id'];
    if (this.budgetId) {
      this.isEdit = true;
      this.budgetService.getById(this.budgetId).subscribe(b => this.form.patchValue(b));
    }
  }

  onSubmit() {
    if (this.form.invalid) return;
    const data = this.form.getRawValue() as any;
    const obs = this.isEdit
      ? this.budgetService.update(this.budgetId, data)
      : this.budgetService.create(data);
    obs.subscribe(() => this.router.navigate(['/budgets']));
  }

  cancel() {
    this.router.navigate(['/budgets']);
  }
}
