import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-procurement-global',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatTabsModule, MtxGridModule, PageHeader, RouterLink],
  template: `
    <page-header title="Suprimentos" subtitle="Visão consolidada de todas as obras" />
    <mat-tab-group>
      <mat-tab label="Pedidos de Compra">
        <mtx-grid [columns]="orderColumns" [data]="orders()" [loading]="loading()"
                  [pageOnFront]="true" [pageSize]="20" />
      </mat-tab>
      <mat-tab label="Pedidos Atrasados">
        <mtx-grid [columns]="orderColumns" [data]="overdue()" [loading]="loading()"
                  [pageOnFront]="true" [pageSize]="20" />
      </mat-tab>
    </mat-tab-group>
  `,
})
export class ProcurementGlobalComponent implements OnInit {
  private readonly http = inject(HttpClient);
  orders = signal<any[]>([]);
  overdue = signal<any[]>([]);
  loading = signal(true);

  orderColumns: MtxGridColumn[] = [
    { header: 'Obra', field: 'projectName', sortable: true },
    { header: 'Número', field: 'number', width: '120px' },
    { header: 'Descrição', field: 'description' },
    { header: 'Fornecedor', field: 'supplierName' },
    { header: 'Valor', field: 'total', width: '130px', formatter: (d: any) => `R$ ${(d.quantity * d.unitPrice).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}` },
    { header: 'Entrega', field: 'expectedDeliveryDate', width: '110px' },
    { header: 'Status', field: 'status', width: '120px', tag: { PENDING: { text: 'Pendente', color: 'orange' }, APPROVED: { text: 'Aprovado', color: 'green' }, RECEIVED: { text: 'Recebido', color: 'default' } } },
  ];

  ngOnInit() {
    // Load from all projects — backend needs a global endpoint, for now load from first project
    this.http.get<any>('/projects?page=0&size=50').subscribe(res => {
      const projects = res.content || [];
      if (projects.length === 0) { this.loading.set(false); return; }
      // Load orders from first active project as demo
      const pid = projects[0].id;
      this.http.get<any>(`/projects/${pid}/procurement/orders`).subscribe(orders => {
        this.orders.set((orders.content || orders).map((o: any) => ({ ...o, projectName: projects[0].name })));
        this.loading.set(false);
      });
      this.http.get<any>(`/projects/${pid}/procurement/orders/overdue`).subscribe(od => {
        this.overdue.set((od || []).map((o: any) => ({ ...o, projectName: projects[0].name })));
      });
    });
  }
}

import { Routes } from '@angular/router';
export const routes: Routes = [{ path: '', component: ProcurementGlobalComponent }];
