import { Component, inject } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';
import { MeasurementService } from '../services/measurement.service';
import { AvailableMeasurementItem } from '../models/measurement.model';

@Component({
  selector: 'app-measurement-form',
  templateUrl: './measurement-form.html',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, PageHeader],
})
export class MeasurementFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(MeasurementService);
  private readonly route2 = inject(ActivatedRoute);
  private projectId = '';
  availableItems: AvailableMeasurementItem[] = [];
  readonly router = inject(Router);

  form = this.fb.nonNullable.group({
    number: [1, [Validators.required, Validators.min(1)]],
    periodStart: ['', Validators.required],
    periodEnd: ['', Validators.required],
    retentionPct: [5, [Validators.min(0), Validators.max(100)]],
    items: this.fb.array([]),
  });

  constructor() {
    this.projectId = this.findProjectId();
    this.service.availableItems(this.projectId).subscribe(items => {
      this.availableItems = items.filter(item => item.balanceQuantity > 0).slice(0, 20);
      this.availableItems.forEach(item => this.items.push(this.fb.nonNullable.group({
        budgetItemId: [item.budgetItemId],
        description: [item.description],
        quantity: [0, [Validators.min(0)]],
        unitPrice: [item.unitPrice, [Validators.min(0)]],
      })));
    });
  }

  get items() {
    return this.form.controls.items as FormArray;
  }

  onSubmit() {
    if (this.form.invalid) return;
    const value = this.form.getRawValue();
    const items = value.items
      .filter((item: any) => Number(item.quantity) > 0)
      .map((item: any) => ({
        budgetItemId: item.budgetItemId,
        description: item.description,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
      }));
    if (items.length === 0) return;
    this.service.create(this.projectId, {
      number: value.number,
      periodStart: value.periodStart,
      periodEnd: value.periodEnd,
      retentionPct: Number((value.retentionPct / 100).toFixed(6)),
      items,
    }).subscribe(() => this.router.navigate(['../'], { relativeTo: this.route2 }));
  }

  cancel() {
    this.router.navigate(['../'], { relativeTo: this.route2 });
  }

  private findProjectId() {
    let route = this.route2.snapshot;
    while (route.parent && !route.paramMap.get('projectId')) route = route.parent;
    return route.paramMap.get('projectId') || '';
  }
}
