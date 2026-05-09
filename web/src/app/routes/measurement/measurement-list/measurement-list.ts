import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { MtxDialog } from '@ng-matero/extensions/dialog';
import { PageHeader } from '@shared';
import { MeasurementService } from '../services/measurement.service';
import { Measurement } from '../models/measurement.model';

@Component({
  selector: 'app-measurement-list',
  templateUrl: './measurement-list.html',
  imports: [MatButtonModule, MatIconModule, MatChipsModule, MtxGridModule, PageHeader],
})
export class MeasurementListComponent implements OnInit {
  private readonly service = inject(MeasurementService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MtxDialog);

  columns: MtxGridColumn[] = [
    { header: '#', field: 'number', width: '60px', sortable: true },
    { header: 'Período Início', field: 'periodStart', width: '130px' },
    { header: 'Período Fim', field: 'periodEnd', width: '130px' },
    {
      header: 'Valor Bruto',
      field: 'grossAmount',
      width: '140px',
      formatter: (d: Measurement) => `R$ ${(d.grossAmount ?? 0).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
    },
    {
      header: 'Valor Líquido',
      field: 'netAmount',
      width: '140px',
      formatter: (d: Measurement) => `R$ ${(d.netAmount ?? 0).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
    },
    {
      header: 'Status',
      field: 'status',
      width: '130px',
      tag: {
        DRAFT: { text: 'Rascunho', color: 'blue' },
        SUBMITTED: { text: 'Enviada', color: 'orange' },
        APPROVED: { text: 'Aprovada', color: 'green' },
        PAID: { text: 'Paga', color: 'default' },
      },
    },
    {
      header: 'Ações',
      field: 'actions',
      width: '160px',
      pinned: 'right',
      type: 'button',
      buttons: [
        {
          type: 'icon', icon: 'send', tooltip: 'Submeter', color: 'primary',
          iif: (d: Measurement) => d.status === 'DRAFT',
          click: (d: Measurement) => this.submit(d),
        },
        {
          type: 'icon', icon: 'check_circle', tooltip: 'Aprovar', color: 'accent',
          iif: (d: Measurement) => d.status === 'SUBMITTED',
          click: (d: Measurement) => this.approve(d),
        },
        {
          type: 'icon', icon: 'visibility', tooltip: 'Visualizar',
          click: (d: Measurement) => this.router.navigate(['/measurements', d.id]),
        },
      ],
    },
  ];

  list: Measurement[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() { this.loadData(); }

  loadData() {
    this.isLoading = true;
    // TODO: get budgetId from route or selection
    this.service.list('', this.query.page, this.query.size).subscribe({
      next: res => { this.list = res.content; this.total = res.totalElements; this.isLoading = false; },
      error: () => { this.isLoading = false; },
    });
  }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }

  submit(m: Measurement) {
    this.dialog.confirm('Submeter medição', `Submeter medição #${m.number} para aprovação?`, () =>
      this.service.submit(m.id).subscribe(() => this.loadData())
    );
  }

  approve(m: Measurement) {
    this.dialog.confirm('Aprovar medição', `Aprovar medição #${m.number}? Isso gerará fatura e transações de custo.`, () =>
      this.service.approve(m.id).subscribe(() => this.loadData())
    );
  }

  create() { this.router.navigate(['/measurements/new']); }
}
