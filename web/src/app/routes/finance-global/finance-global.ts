import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-finance-global',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatTabsModule, MtxGridModule, PageHeader],
  template: `
    <page-header title="Financeiro" subtitle="Visão consolidada de todas as obras" />
    <mat-tab-group>
      <mat-tab label="Contas a Pagar">
        <mtx-grid [columns]="payableColumns" [data]="payables()" [loading]="loading()"
                  [pageOnFront]="true" [pageSize]="20" />
      </mat-tab>
      <mat-tab label="Contas a Receber">
        <mtx-grid [columns]="receivableColumns" [data]="receivables()" [loading]="loading()"
                  [pageOnFront]="true" [pageSize]="20" />
      </mat-tab>
    </mat-tab-group>
  `,
})
export class FinanceGlobalComponent implements OnInit {
  private readonly http = inject(HttpClient);
  payables = signal<any[]>([]);
  receivables = signal<any[]>([]);
  loading = signal(true);

  payableColumns: MtxGridColumn[] = [
    { header: 'Obra', field: 'projectName', sortable: true },
    { header: 'Descrição', field: 'description' },
    { header: 'Fornecedor', field: 'supplierName' },
    { header: 'Valor', field: 'amount', width: '130px', formatter: (d: any) => `R$ ${d.amount?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) || '0'}` },
    { header: 'Vencimento', field: 'dueDate', width: '110px' },
    { header: 'Status', field: 'status', width: '110px', tag: { PENDING: { text: 'Pendente', color: 'orange' }, PAID: { text: 'Pago', color: 'green' }, OVERDUE: { text: 'Vencido', color: 'red' } } },
  ];

  receivableColumns: MtxGridColumn[] = [
    { header: 'Obra', field: 'projectName', sortable: true },
    { header: 'Descrição', field: 'description' },
    { header: 'Valor', field: 'amount', width: '130px', formatter: (d: any) => `R$ ${d.amount?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) || '0'}` },
    { header: 'Vencimento', field: 'dueDate', width: '110px' },
    { header: 'Status', field: 'status', width: '110px', tag: { PENDING: { text: 'Pendente', color: 'orange' }, RECEIVED: { text: 'Recebido', color: 'green' }, OVERDUE: { text: 'Vencido', color: 'red' } } },
  ];

  ngOnInit() {
    this.http.get<any>('/projects?page=0&size=50').subscribe(res => {
      const projects = res.content || [];
      if (projects.length === 0) { this.loading.set(false); return; }
      const pid = projects[0].id;
      this.http.get<any>(`/projects/${pid}/finance/payables`).subscribe(p => {
        this.payables.set((p.content || p || []).map((x: any) => ({ ...x, projectName: projects[0].name })));
        this.loading.set(false);
      });
      this.http.get<any>(`/projects/${pid}/finance/receivables`).subscribe(r => {
        this.receivables.set((r.content || r || []).map((x: any) => ({ ...x, projectName: projects[0].name })));
      });
    });
  }
}

import { Routes } from '@angular/router';
export const routes: Routes = [{ path: '', component: FinanceGlobalComponent }];
