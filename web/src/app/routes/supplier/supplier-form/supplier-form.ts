import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { PageHeader } from '@shared';
import { SupplierService } from '../services/supplier.service';

@Component({
  selector: 'app-supplier-form',
  templateUrl: './supplier-form.html',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatCheckboxModule, MatButtonModule, MatSelectModule, PageHeader],
})
export class SupplierFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(SupplierService);
  private readonly route = inject(ActivatedRoute);
  readonly router = inject(Router);

  isEdit = false;
  private id = '';
  readonly categories = [
    { value: 'GENERAL', label: 'Geral' },
    { value: 'MATERIAL', label: 'Materiais' },
    { value: 'SERVICE', label: 'Serviços' },
    { value: 'EQUIPMENT', label: 'Equipamentos' },
    { value: 'SUBCONTRACTOR', label: 'Subempreiteiro' },
  ];
  readonly qualificationStatuses = [
    { value: 'APPROVED', label: 'Homologado' },
    { value: 'UNDER_REVIEW', label: 'Em revisão' },
    { value: 'BLOCKED', label: 'Bloqueado' },
    { value: 'PROSPECT', label: 'Prospect' },
  ];

  form = this.fb.nonNullable.group({
    code: ['', [Validators.required, Validators.maxLength(40)]],
    name: ['', [Validators.required, Validators.maxLength(140)]],
    tradeName: [''],
    taxId: ['', [Validators.required, Validators.maxLength(30)]],
    email: ['', Validators.email],
    phone: [''],
    contactName: [''],
    website: [''],
    category: ['GENERAL', [Validators.required]],
    qualificationStatus: ['APPROVED', [Validators.required]],
    paymentTermDays: [28, [Validators.required, Validators.min(0), Validators.max(365)]],
    leadTimeDays: [7, [Validators.required, Validators.min(0), Validators.max(365)]],
    address: [''],
    city: [''],
    state: ['', [Validators.maxLength(2), Validators.pattern(/^[A-Za-z]{0,2}$/)]],
    postalCode: [''],
    notes: [''],
    rating: [5, [Validators.min(1), Validators.max(10)]],
    active: [true],
  });

  ngOnInit() {
    this.id = this.route.snapshot.params['id'];
    if (this.id) {
      this.isEdit = true;
      this.service.getById(this.id).subscribe(s => this.form.patchValue(s));
    }
  }

  onSubmit() {
    if (this.form.invalid) return;
    const data = {
      ...this.form.getRawValue(),
      state: this.form.getRawValue().state.toUpperCase(),
    } as any;
    const obs = this.isEdit ? this.service.update(this.id, data) : this.service.create(data);
    obs.subscribe(() => this.router.navigate(['/suppliers']));
  }
}
