import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';
import { SupplierService } from '../services/supplier.service';

@Component({
  selector: 'app-supplier-form',
  templateUrl: './supplier-form.html',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatCheckboxModule, MatButtonModule, PageHeader],
})
export class SupplierFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(SupplierService);
  private readonly route = inject(ActivatedRoute);
  readonly router = inject(Router);

  isEdit = false;
  private id = '';

  form = this.fb.nonNullable.group({
    code: ['', [Validators.required, Validators.maxLength(40)]],
    name: ['', [Validators.required, Validators.maxLength(140)]],
    tradeName: [''],
    taxId: ['', [Validators.required, Validators.maxLength(30)]],
    email: ['', Validators.email],
    phone: [''],
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
    const data = this.form.getRawValue() as any;
    const obs = this.isEdit ? this.service.update(this.id, data) : this.service.create(data);
    obs.subscribe(() => this.router.navigate(['/suppliers']));
  }
}
