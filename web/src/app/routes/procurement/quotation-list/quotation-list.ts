import { Component, inject, OnInit } from '@angular/core';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { ProcurementService } from '../services/procurement.service';
import { Quotation } from '../models/procurement.model';

@Component({
  selector: 'app-quotation-list',
  templateUrl: './quotation-list.html',
  imports: [MtxGridModule, PageHeader],
})
export class QuotationListComponent implements OnInit {
  private readonly service = inject(ProcurementService);

  columns: MtxGridColumn[] = [
    { header: 'ID', field: 'id', width: '100px' },
    { header: 'Solicitação', field: 'purchaseRequest', sortable: true },
    { header: 'Prazo', field: 'deadline', width: '130px' },
    { header: 'Status', field: 'status', width: '110px', tag: { OPEN: { text: 'Aberta', color: 'blue' }, CLOSED: { text: 'Fechada', color: 'green' } } },
    { header: 'Respostas', field: 'responsesCount', width: '110px' },
  ];

  list: Quotation[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() { this.loadData(); }

  loadData() {
    this.isLoading = true;
    this.service.listQuotations(this.query.page, this.query.size).subscribe({
      next: res => { this.list = res.content; this.total = res.totalElements; this.isLoading = false; },
      error: () => (this.isLoading = false),
    });
  }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }
}
