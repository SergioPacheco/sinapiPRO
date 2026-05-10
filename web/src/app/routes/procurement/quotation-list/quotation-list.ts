import { ActivatedRoute } from '@angular/router';
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
  private readonly route = inject(ActivatedRoute);
  private projectId = '';

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

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get("projectId")) r = r.parent; this.projectId = r.paramMap.get("projectId") || ""; this.projectId = this.route.parent!.parent!.snapshot.paramMap.get('projectId')!; this.loadData(); }

  loadData() {
    this.isLoading = true;
    this.service.listQuotations(this.projectId, this.query.page, this.query.size).subscribe({
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
