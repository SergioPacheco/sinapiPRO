import { Component, inject, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { map } from 'rxjs';
import { PageHeader, LookupFieldComponent, SearchDialogComponent, QuickCreateDialogComponent } from '@shared';
import { ProcurementService } from '../services/procurement.service';
import { PurchaseOrder } from '../models/procurement.model';
import { SupplierService } from '../../supplier/services/supplier.service';
import { Supplier } from '../../supplier/models/supplier.model';

@Component({
  selector: 'app-procurement-list',
  templateUrl: './procurement-list.html',
  imports: [
    FormsModule, MatButtonModule, MatIconModule, MatCardModule, MatFormFieldModule,
    MatInputModule, MatDialogModule, MtxGridModule, PageHeader, LookupFieldComponent,
  ],
  styles: `
    .workflow-stepper { display: flex; align-items: center; justify-content: center; gap: 8px; padding: 16px; margin-bottom: 16px; background: var(--mat-sys-surface-container); border-radius: 12px; }
    .workflow-step { display: flex; flex-direction: column; align-items: center; gap: 4px; }
    .step-circle { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-weight: 700; font-size: 14px; }
    .step-label { font-size: 11px; font-weight: 500; text-transform: uppercase; color: var(--mat-sys-on-surface-variant); }
    .step-arrow { color: var(--mat-sys-outline); margin-bottom: 18px; }
  `,
})
export class ProcurementListComponent implements OnInit {
  private readonly service = inject(ProcurementService);
  private readonly supplierService = inject(SupplierService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private projectId = '';

  @ViewChild('supplierFormTpl') supplierFormTpl!: TemplateRef<any>;

  columns: MtxGridColumn[] = [
    { header: 'Número', field: 'number', sortable: true, width: '120px' },
    { header: 'Descrição', field: 'description', sortable: true },
    { header: 'Fornecedor', field: 'supplierName', sortable: true },
    { header: 'Qtd', field: 'quantity', width: '80px' },
    {
      header: 'Preço Unit.',
      field: 'unitPrice',
      width: '130px',
      formatter: (data: PurchaseOrder) =>
        `R$ ${data.unitPrice.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
    },
    {
      header: 'Total',
      field: 'totalAmount',
      width: '130px',
      formatter: (data: PurchaseOrder) =>
        `R$ ${data.totalAmount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
    },
    {
      header: 'Status',
      field: 'status',
      width: '130px',
      tag: {
        PENDING: { text: 'Pendente', color: 'orange' },
        PARTIAL: { text: 'Parcial', color: 'blue' },
        RECEIVED: { text: 'Recebido', color: 'green' },
      },
    },
    {
      header: '',
      field: 'actions',
      width: '140px',
      type: 'button',
      buttons: [
        {
          type: 'icon',
          icon: 'picture_as_pdf',
          tooltip: 'Pedido PDF',
          click: (record: PurchaseOrder) => this.downloadPdf(record),
        },
        {
          type: 'icon',
          icon: 'request_quote',
          tooltip: 'Ver cotações',
          click: (record: PurchaseOrder) => this.openQuotations(record),
        },
      ],
    },
  ];

  list: PurchaseOrder[] = [];
  overdue: PurchaseOrder[] = [];
  quotationCount = 0;
  abcDraft = { description: '', quantity: 1, unit: 'UN' };
  abcItems: { description: string; quantity: number; unit: string }[] = [];
  abcSupplierId = '';
  abcSupplierDisplay = '';
  newSupplier: Partial<Supplier> = {};
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() {
    let r = this.route.snapshot;
    while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.service.listOrders(this.projectId, this.query.page, this.query.size).subscribe({
      next: res => {
        this.list = res.content;
        this.total = res.totalElements;
        this.isLoading = false;
      },
      error: () => (this.isLoading = false),
    });
    this.service.listOverdue(this.projectId).subscribe(res => this.overdue = res);
    this.service.listQuotations(this.projectId, 0, 1).subscribe(res => this.quotationCount = res.totalElements);
  }

  getByStatus(status: string) { return this.list.filter(o => o.status === status); }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }

  downloadPdf(order: PurchaseOrder) {
    window.open(this.service.orderReportUrl(this.projectId, order.id), '_blank');
  }

  openQuotations(order: PurchaseOrder) {
    this.router.navigate(['../quotations'], {
      relativeTo: this.route,
      queryParams: { orderId: order.id, orderNumber: order.number },
    });
  }

  addAbcItem() {
    if (!this.abcDraft.description.trim() || !this.abcDraft.quantity || !this.abcDraft.unit.trim()) return;
    this.abcItems = [...this.abcItems, {
      description: this.abcDraft.description.trim(),
      quantity: this.abcDraft.quantity,
      unit: this.abcDraft.unit.trim(),
    }];
    this.abcDraft = { description: '', quantity: 1, unit: 'UN' };
  }

  generateFromAbc() {
    if (this.abcItems.length === 0) return;
    this.service.generateFromAbc(this.projectId, this.abcItems).subscribe(() => {
      this.abcItems = [];
      this.loadData();
    });
  }

  removeAbcItem(index: number) {
    this.abcItems = this.abcItems.filter((_, i) => i !== index);
  }

  // --- Supplier Lookup ---
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
        this.abcSupplierId = supplier.id;
        this.abcSupplierDisplay = supplier.name;
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
          this.abcSupplierId = s.id;
          this.abcSupplierDisplay = s.name;
        });
      }
    });
  }

  clearSupplier() {
    this.abcSupplierId = '';
    this.abcSupplierDisplay = '';
  }
}
