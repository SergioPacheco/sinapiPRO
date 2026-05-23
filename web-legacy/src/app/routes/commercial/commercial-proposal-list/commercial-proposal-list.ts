import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { MtxDialog } from '@ng-matero/extensions/dialog';
import { PageHeader } from '@shared';
import { NextActionService } from '@shared';

interface Proposal {
  id: string; title: string; clientName: string; clientDocument: string;
  totalValue: number; proposalDate: string; status: string; validityDays: number;
  address: string; city: string; state: string;
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
  private readonly router = inject(Router);
  private readonly dialog = inject(MtxDialog);
  private readonly nextAction = inject(NextActionService);
  list = signal<Proposal[]>([]);
  loading = signal(true);

  columns: MtxGridColumn[] = [
    { header: 'Título', field: 'title', sortable: true },
    { header: 'Cliente', field: 'clientName', sortable: true },
    { header: 'Valor', field: 'totalValue', width: '140px', formatter: (d: Proposal) => d.totalValue ? `R$ ${d.totalValue.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}` : '—' },
    { header: 'Data', field: 'proposalDate', width: '110px' },
    { header: 'Status', field: 'status', width: '120px', tag: { DRAFT: { text: 'Rascunho', color: 'default' }, SENT: { text: 'Enviada', color: 'blue' }, ACCEPTED: { text: 'Aceita', color: 'green' }, REJECTED: { text: 'Recusada', color: 'red' } } },
    {
      header: '', field: 'actions', width: '140px', type: 'button',
      buttons: [
        {
          type: 'icon', icon: 'check_circle', tooltip: 'Aprovar proposta', color: 'accent',
          iif: (d: Proposal) => d.status === 'SENT',
          click: (d: Proposal) => this.approve(d),
        },
        {
          type: 'icon', icon: 'add_business', tooltip: 'Converter em Obra', color: 'primary',
          iif: (d: Proposal) => d.status === 'ACCEPTED',
          click: (d: Proposal) => this.convertToProject(d),
        },
      ],
    },
  ];

  ngOnInit() {
    this.http.get<any>('/proposals').subscribe({
      next: res => { this.list.set(res.content || res || []); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  approve(proposal: Proposal) {
    this.dialog.confirm('Aprovar proposta', `Aprovar "${proposal.title}"?`, () => {
      this.http.post(`/proposals/${proposal.id}/accept`, {}).subscribe(() => {
        proposal.status = 'ACCEPTED';
        this.list.set([...this.list()]);
      });
    });
  }

  convertToProject(proposal: Proposal) {
    this.dialog.confirm(
      'Converter em Obra',
      `Criar obra a partir da proposta "${proposal.title}" (${proposal.clientName})?`,
      () => {
        const payload = {
          code: `OBR-${Date.now().toString(36).toUpperCase()}`,
          name: proposal.title,
          customerName: proposal.clientName,
          customerDocument: proposal.clientDocument || '',
          address: proposal.address || '',
          city: proposal.city || '',
          state: proposal.state || '',
          totalBudget: proposal.totalValue,
          description: `Gerada a partir da proposta comercial ${proposal.id}`,
        };
        this.http.post<any>('/projects', payload).subscribe(project => {
          this.router.navigate(['/projects', project.id]);
          this.nextAction.suggest('project.created', `/projects/${project.id}`);
        });
      },
    );
  }

  create() { /* TODO: open proposal form */ }
}
