import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';
import { BudgetService } from '../services/budget.service';

@Component({
  selector: 'app-budget-form',
  templateUrl: './budget-form.html',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, PageHeader],
})
export class BudgetFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly budgetService = inject(BudgetService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  isEdit = false;
  private projectId = '';
  private budgetId = '';

  form = this.fb.nonNullable.group({
    code: ['', [Validators.required, Validators.maxLength(40)]],
    title: ['', [Validators.required, Validators.maxLength(140)]],
    customerName: ['', [Validators.required, Validators.maxLength(140)]],
    totalAmount: [0, [Validators.required, Validators.min(0)]],
    status: ['DRAFT' as string, Validators.required],
    startDate: ['', Validators.required],
    endDate: [''],
  });

  statuses = [
    { value: 'DRAFT', label: 'Rascunho' },
    { value: 'IN_REVIEW', label: 'Em análise' },
    { value: 'APPROVED', label: 'Aprovado' },
    { value: 'REJECTED', label: 'Reprovado' },
    { value: 'SUPERSEDED', label: 'Substituído' },
    { value: 'IN_EXECUTION', label: 'Em execução' },
    { value: 'COMPLETED', label: 'Concluído' },
    { value: 'CANCELLED', label: 'Cancelado' },
  ];

  ngOnInit() {
    let route = this.route.snapshot;
    while (route.parent && !route.paramMap.get('projectId')) route = route.parent;
    this.projectId = route.paramMap.get('projectId') || '';
    this.budgetId = this.route.snapshot.params['id'] || '';
    this.isEdit = !!this.budgetId;
    if (this.isEdit) {
      this.budgetService.getById(this.projectId, this.budgetId).subscribe(b => this.form.patchValue(b));
    }
  }

  onSubmit() {
    if (this.form.invalid) return;
    const data = this.form.getRawValue() as any;
    const obs = this.isEdit
      ? this.budgetService.update(this.projectId, this.budgetId, data)
      : this.budgetService.create(this.projectId, data);
    obs.subscribe(() => this.router.navigate(['../../budgets'], { relativeTo: this.route }));
  }

  cancel() { this.router.navigate(['../../budgets'], { relativeTo: this.route }); }
}
