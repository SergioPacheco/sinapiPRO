import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { PageHeader } from '@shared';
import { Employee } from '../models/registry.model';
import { RegistryService } from '../services/registry.service';

@Component({
  selector: 'app-employee-form',
  templateUrl: './employee-form.html',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    PageHeader,
  ],
})
export class EmployeeFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(RegistryService);
  private readonly route = inject(ActivatedRoute);
  readonly router = inject(Router);

  isEdit = false;
  private id = '';

  readonly typeOptions = [
    { value: 'EMPLOYEE', label: 'Funcionário' },
    { value: 'CONTRACTOR', label: 'Empreiteiro' },
  ];
  readonly statusOptions = [
    { value: 'ACTIVE', label: 'Ativo' },
    { value: 'ON_LEAVE', label: 'Afastado' },
    { value: 'INACTIVE', label: 'Inativo' },
  ];

  form = this.fb.nonNullable.group({
    employeeCode: ['', [Validators.required, Validators.maxLength(30)]],
    name: ['', [Validators.required, Validators.maxLength(200)]],
    document: [''],
    role: ['', [Validators.required, Validators.maxLength(80)]],
    specialty: ['', [Validators.required, Validators.maxLength(100)]],
    type: ['EMPLOYEE'],
    employmentStatus: ['ACTIVE'],
    email: ['', Validators.email],
    phone: [''],
    mobilePhone: [''],
    emergencyContactName: [''],
    emergencyContactPhone: [''],
    address: [''],
    city: [''],
    state: ['', [Validators.maxLength(2), Validators.pattern(/^[A-Za-z]{0,2}$/)]],
    postalCode: [''],
    costCenter: [''],
    companyName: [''],
    notes: [''],
    hourlyRate: [0 as number | null],
    admissionDate: [''],
    terminationDate: [''],
  });

  ngOnInit() {
    this.id = this.route.snapshot.params['id'];
    if (this.id) {
      this.isEdit = true;
      this.service.getEmployee(this.id).subscribe(employee => this.form.patchValue(employee as any));
    }
  }

  onSubmit() {
    if (this.form.invalid) return;
    const raw = this.form.getRawValue();
    const payload: Partial<Employee> = {
      ...raw,
      type: raw.type as Employee['type'],
      employmentStatus: raw.employmentStatus as Employee['employmentStatus'],
      state: raw.state.toUpperCase(),
      hourlyRate: raw.hourlyRate || null,
      admissionDate: raw.admissionDate || null,
      terminationDate: raw.terminationDate || null,
      companyName: raw.companyName || '',
    };
    const request$ = this.isEdit
      ? this.service.updateEmployee(this.id, payload)
      : this.service.createEmployee(payload);
    request$.subscribe(() => this.router.navigate(['/registry/employees']));
  }
}
