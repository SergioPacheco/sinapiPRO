import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { CommercialService } from '../services/commercial.service';
import { SalesProposal } from '../models/commercial.model';

@Component({
  selector: 'app-proposal-list',
  template: `
    <page-header title="Propostas de Venda" subtitle="Gestão de propostas e contratos" />
    <mtx-grid [columns]="columns" [data]="list()" [length]="total()" [loading]="isLoading()"
      [pageOnFront]="false" [pageIndex]="page()" [pageSize]="size()" [pageSizeOptions]="[10, 20, 50]"
      (page)="onPageChange($event)" [rowStriped]="true" />
  `,
  imports: [MatButtonModule, MtxGridModule, PageHeader],
})
export class ProposalListComponent implements OnInit {
  private readonly service = inject(CommercialService);
  private readonly route = inject(ActivatedRoute);
  private devId = '';

  list = signal<SalesProposal[]>([]);
  total = signal(0);
  isLoading = signal(true);
  page = signal(0);
  size = signal(20);

  columns: MtxGridColumn[] = [
    { header: 'Unidade', field: 'unitCode', width: '100px' },
    { header: 'Cliente', field: 'clientName', sortable: true },
    { header: 'Data', field: 'proposalDate', width: '110px' },
    { header: 'Valor', field: 'proposedPrice', width: '140px', formatter: (d: SalesProposal) => `R$ ${d.proposedPrice?.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}` },
    { header: 'Parcelas', field: 'installments', width: '90px' },
    { header: 'Status', field: 'status', width: '130px', tag: { PENDING: { text: 'Pendente', color: 'orange' }, APPROVED: { text: 'Aprovada', color: 'blue' }, SIGNED: { text: 'Assinada', color: 'green' }, REJECTED: { text: 'Rejeitada', color: 'red' }, CANCELLED: { text: 'Cancelada', color: '' } } },
  ];

  ngOnInit() {
    this.devId = this.route.snapshot.paramMap.get('devId') || '';
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);
    this.service.listProposals(this.devId, this.page(), this.size()).subscribe({
      next: res => { this.list.set(res.content); this.total.set(res.totalElements); this.isLoading.set(false); },
      error: () => this.isLoading.set(false),
    });
  }

  onPageChange(e: any) { this.page.set(e.pageIndex); this.size.set(e.pageSize); this.loadData(); }
}
