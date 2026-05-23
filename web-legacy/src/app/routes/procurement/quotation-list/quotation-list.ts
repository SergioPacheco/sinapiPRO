import { ActivatedRoute } from '@angular/router';
import { Component, inject, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { ProcurementService } from '../services/procurement.service';
import { Quotation } from '../models/procurement.model';

@Component({
  selector: 'app-quotation-list',
  templateUrl: './quotation-list.html',
  imports: [MatCardModule, MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class QuotationListComponent implements OnInit {
  private readonly service = inject(ProcurementService);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';
  private selectedOrderId = '';
  selectedOrderNumber = '';

  columns: MtxGridColumn[] = [
    { header: 'ID', field: 'id', width: '100px' },
    { header: 'Solicitação', field: 'purchaseRequest', sortable: true },
    { header: 'Prazo', field: 'deadline', width: '130px' },
    { header: 'Status', field: 'status', width: '110px', tag: { OPEN: { text: 'Aberta', color: 'blue' }, CLOSED: { text: 'Fechada', color: 'green' } } },
    { header: 'Respostas', field: 'responsesCount', width: '110px' },
    {
      header: '',
      field: 'actions',
      width: '120px',
      type: 'button',
      buttons: [
        {
          type: 'icon',
          icon: 'email',
          tooltip: 'Enviar por email',
          click: (record: Quotation) => this.sendEmail(record),
        },
        {
          type: 'icon',
          icon: 'picture_as_pdf',
          tooltip: 'Mapa comparativo',
          click: (record: Quotation) => this.openComparativeMap(record),
        },
      ],
    },
  ];

  list: Quotation[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() {
    let r = this.route.snapshot;
    while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.selectedOrderId = this.route.snapshot.queryParamMap.get('orderId') || '';
    this.selectedOrderNumber = this.route.snapshot.queryParamMap.get('orderNumber') || '';
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.service.listQuotations(this.projectId, this.query.page, this.query.size, this.selectedOrderId || undefined).subscribe({
      next: res => { this.list = res.content; this.total = res.totalElements; this.isLoading = false; },
      error: () => (this.isLoading = false),
    });
  }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }

  sendEmail(quotation: Quotation) {
    this.service.sendQuotationEmail(this.projectId, quotation.id).subscribe(() => this.loadData());
  }

  openComparativeMap(quotation: Quotation) {
    window.open(this.service.comparativeMapUrl(this.projectId, quotation.id), '_blank');
  }
}
