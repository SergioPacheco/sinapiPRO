import { Component, inject, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { map } from 'rxjs';
import { PageHeader, LookupFieldComponent, SearchDialogComponent, QuickCreateDialogComponent } from '@shared';
import { NextActionService } from '@shared';
import { ContractService } from '../services/contract.service';
import { SupplierService } from '../../supplier/services/supplier.service';
import { Supplier } from '../../supplier/models/supplier.model';

@Component({
  selector: 'app-contract-form',
  templateUrl: './contract-form.html',
  imports: [
    ReactiveFormsModule, FormsModule, MatCardModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatDialogModule, PageHeader, LookupFieldComponent,
  ],
})
export class ContractFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(ContractService);
  private readonly supplierService = inject(SupplierService);
  private readonly route = inject(ActivatedRoute);
  private readonly dialog = inject(MatDialog);
  private readonly nextAction = inject(NextActionService);
  private projectId = '';
  readonly router = inject(Router);

  @ViewChild('supplierFormTpl') supplierFormTpl!: TemplateRef<any>;

  isEdit = false;
  private id = '';

  supplierDisplay = '';
  newSupplier: Partial<Supplier> = {};

  form = this.fb.nonNullable.group({
    number: ['', Validators.required],
    description: ['', Validators.required],
    supplierId: [''],
    originalValue: [0, [Validators.required, Validators.min(0)]],
    retentionPct: [0.05, [Validators.min(0), Validators.max(1)]],
    status: ['DRAFT' as string, Validators.required],
    startDate: ['', Validators.required],
    endDate: [''],
  });

  ngOnInit() {
    this.projectId = this.route.parent!.parent!.snapshot.paramMap.get('projectId')!;
    this.id = this.route.snapshot.params['id'];
    if (this.id) {
      this.isEdit = true;
      this.service.getById(this.projectId, this.id).subscribe(c => {
        this.form.patchValue(c);
        this.supplierDisplay = (c as any).supplierName || '';
      });
    }
  }

  searchSupplier() {
    const ref = this.dialog.open(SearchDialogComponent, {
      data: {
        title: 'Pesquisar Fornecedor',
        columns: [
          { key: 'name', label: 'Nome' },
          { key: 'taxId', label: 'CNPJ' },
          { key: 'category', label: 'Categoria' },
        ],
        displayFn: (s: Supplier) => s.name,
        searchFn: (term: string) => this.supplierService.search(term).pipe(map(r => r.content)),
      },
    });
    ref.afterClosed().subscribe((supplier: Supplier | undefined) => {
      if (supplier) {
        this.form.patchValue({ supplierId: supplier.id });
        this.supplierDisplay = supplier.name;
      }
    });
  }

  createSupplier() {
    this.newSupplier = { category: 'GENERAL', qualificationStatus: 'PROSPECT' };
    const ref = this.dialog.open(QuickCreateDialogComponent, {
      data: { title: 'Cadastrar Fornecedor', formTemplate: this.supplierFormTpl },
    });
    ref.afterClosed().subscribe(result => {
      if (result?.action === 'save' && this.newSupplier.name) {
        this.supplierService.create(this.newSupplier).subscribe(s => {
          this.form.patchValue({ supplierId: s.id });
          this.supplierDisplay = s.name;
        });
      }
    });
  }

  clearSupplier() {
    this.form.patchValue({ supplierId: '' });
    this.supplierDisplay = '';
  }

  onSubmit() {
    if (this.form.invalid) return;
    const data = this.form.getRawValue() as any;
    const obs = this.isEdit ? this.service.update(this.projectId, this.id, data) : this.service.create(this.projectId, data);
    obs.subscribe(() => {
      this.router.navigate(['/budgets', this.projectId, 'contracts']);
      if (!this.isEdit) this.nextAction.suggest('contract.created');
    });
  }
}
