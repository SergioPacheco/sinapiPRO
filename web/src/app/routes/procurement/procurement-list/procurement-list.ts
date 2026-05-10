import { ActivatedRoute } from '@angular/router';
import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { ProcurementService } from '../services/procurement.service';
import { PurchaseOrder } from '../models/procurement.model';

@Component({
  selector: 'app-procurement-list',
  templateUrl: './procurement-list.html',
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class ProcurementListComponent implements OnInit {
  private readonly service = inject(ProcurementService);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';

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
        `R$ ${(data.quantity * data.unitPrice).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
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
  ];

  list: PurchaseOrder[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get("projectId")) r = r.parent; this.projectId = r.paramMap.get("projectId") || "";
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
  }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }
}
