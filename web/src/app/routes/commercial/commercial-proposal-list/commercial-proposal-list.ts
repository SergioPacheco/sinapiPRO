import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';

interface Proposal {
  id: string; title: string; clientName: string; totalValue: number;
  proposalDate: string; status: string; validityDays: number;
}

@Component({
  selector: 'app-commercial-proposal-list',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
  template: `
    <page-header title="Propostas Comerciais" subtitle="Propostas formais para clientes">
      <button mat-flat-button color="primary" (click)="create()"><mat-icon>add</mat-icon> Nova Proposta</button>
    </page-header>
    <mtx-grid [columns]="columns" [data]="list()" [loading]="loading()"
              [pageOnFront]="true" [pageSize]="20" />
  `,
})
export class CommercialProposalListComponent implements OnInit {
  private readonly http = inject(HttpClient);
  list = signal<Proposal[]>([]);
  loading = signal(true);

  columns: MtxGridColumn[] = [
    { header: 'Título', field: 'title', sortable: true },
    { header: 'Cliente', field: 'clientName', sortable: true },
    { header: 'Valor', field: 'totalValue', width: '140px', formatter: (d: Proposal) => d.totalValue ? `R$ ${d.totalValue.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}` : '—' },
    { header: 'Data', field: 'proposalDate', width: '110px' },
    { header: 'Validade', field: 'validityDays', width: '90px', formatter: (d: Proposal) => d.validityDays ? `${d.validityDays} dias` : '—' },
    { header: 'Status', field: 'status', width: '120px', tag: { DRAFT: { text: 'Rascunho', color: 'default' }, SENT: { text: 'Enviada', color: 'blue' }, ACCEPTED: { text: 'Aceita', color: 'green' }, REJECTED: { text: 'Recusada', color: 'red' } } },
  ];

  ngOnInit() {
    this.http.get<any>('/proposals').subscribe({
      next: res => { this.list.set(res.content || res || []); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  create() { /* TODO: open form */ }
}
