import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';

interface Invoice {
  id: string; number: string; supplierName: string;
  amount: number; issueDate: string; dueDate: string; status: string;
}

@Component({
  selector: 'app-invoice-list',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
  template: `
    <page-header title="Notas Fiscais" subtitle="Controle de notas fiscais da obra">
      <button mat-flat-button color="primary" (click)="create()"><mat-icon>add</mat-icon> Nova NF</button>
    </page-header>
    <mtx-grid [columns]="columns" [data]="list" [loading]="isLoading"
              [pageOnFront]="true" [pageSize]="20" [pageSizeOptions]="[10,20,50]" />
  `,
})
export class InvoiceListComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';

  columns: MtxGridColumn[] = [
    { header: 'Número', field: 'number', sortable: true, width: '120px' },
    { header: 'Fornecedor', field: 'supplierName', sortable: true },
    {
      header: 'Valor',
      field: 'amount',
      width: '140px',
      formatter: (d: Invoice) => `R$ ${d.amount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
    },
    { header: 'Emissão', field: 'issueDate', width: '110px' },
    { header: 'Vencimento', field: 'dueDate', width: '110px' },
    {
      header: 'Status',
      field: 'status',
      width: '120px',
      tag: {
        PENDING: { text: 'Pendente', color: 'orange' },
        PAID: { text: 'Paga', color: 'green' },
        OVERDUE: { text: 'Vencida', color: 'red' },
        CANCELLED: { text: 'Cancelada', color: 'default' },
      },
    },
  ];

  list: Invoice[] = [];
  isLoading = true;

  ngOnInit() {
    let r = this.route.snapshot;
    while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.http.get<any>(`/invoices?budgetId=${this.projectId}`).subscribe({
      next: res => { this.list = res.content || res; this.isLoading = false; },
      error: () => this.isLoading = false,
    });
  }

  create() { /* TODO: open dialog */ }
}
