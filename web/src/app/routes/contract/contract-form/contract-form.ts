import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';
import { ContractService } from '../services/contract.service';

@Component({
  selector: 'app-contract-form',
  templateUrl: './contract-form.html',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, PageHeader],
})
export class ContractFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(ContractService);
  private readonly route = inject(ActivatedRoute);
  readonly router = inject(Router);

  isEdit = false;
  private id = '';

  form = this.fb.nonNullable.group({
    number: ['', Validators.required],
    description: ['', Validators.required],
    originalValue: [0, [Validators.required, Validators.min(0)]],
    retentionPct: [0.05, [Validators.min(0), Validators.max(1)]],
    status: ['DRAFT' as string, Validators.required],
    startDate: ['', Validators.required],
    endDate: [''],
  });

  ngOnInit() {
    this.id = this.route.snapshot.params['id'];
    if (this.id) {
      this.isEdit = true;
      this.service.getById(this.id).subscribe(c => this.form.patchValue(c));
    }
  }

  onSubmit() {
    if (this.form.invalid) return;
    const data = this.form.getRawValue() as any;
    const obs = this.isEdit ? this.service.update(this.id, data) : this.service.create(data);
    obs.subscribe(() => this.router.navigate(['/contracts']));
  }
}
