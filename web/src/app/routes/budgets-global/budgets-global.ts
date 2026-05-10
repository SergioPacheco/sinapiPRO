import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { Routes } from '@angular/router';

interface BudgetRow {
  id: string; code: string; title: string; customerName: string;
  totalAmount: number; status: string; active: boolean; projectId: string; projectName: string;
}

@Component({
  selector: 'app-budgets-global',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
  template: `
    <page-header title="Orçamentos" subtitle="Todos os orçamentos do portfólio" />
    <mtx-grid [columns]="columns" [data]="list()" [loading]="loading()"
              [pageOnFront]="true" [pageSize]="20" [pageSizeOptions]="[10,20,50]"
              [rowSelectable]="true" (rowClick)="open($event)" />
  `,
})
export class BudgetsGlobalComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  list = signal<BudgetRow[]>([]);
  loading = signal(true);

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'code', sortable: true, width: '120px' },
    { header: 'Título', field: 'title', sortable: true },
    { header: 'Obra', field: 'projectName', sortable: true },
    { header: 'Cliente', field: 'customerName', width: '160px' },
    { header: 'Valor', field: 'totalAmount', width: '140px', formatter: (d: BudgetRow) => `R$ ${d.totalAmount?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) || '0'}` },
    { header: 'Vigente', field: 'active', width: '80px', formatter: (d: BudgetRow) => d.active ? '✓' : '' },
    { header: 'Status', field: 'status', width: '120px', tag: { DRAFT: { text: 'Rascunho', color: 'default' }, IN_REVIEW: { text: 'Em Análise', color: 'orange' }, APPROVED: { text: 'Aprovado', color: 'green' }, IN_EXECUTION: { text: 'Em Execução', color: 'blue' } } },
  ];

  ngOnInit() {
    this.http.get<any>('/projects?page=0&size=100').subscribe(res => {
      const projects = res.content || [];
      const allBudgets: BudgetRow[] = [];
      let pending = projects.length;
      if (pending === 0) { this.loading.set(false); return; }
      projects.forEach((p: any) => {
        this.http.get<any>(`/projects/${p.id}/budgets`).subscribe({
          next: budgets => {
            (budgets.content || budgets || []).forEach((b: any) => {
              allBudgets.push({ ...b, projectId: p.id, projectName: p.name });
            });
            if (--pending === 0) { this.list.set(allBudgets); this.loading.set(false); }
          },
          error: () => { if (--pending === 0) { this.list.set(allBudgets); this.loading.set(false); } },
        });
      });
    });
  }

  open(event: any) {
    const row = event.rowData;
    this.router.navigate(['/projects', row.projectId, 'budgets', row.id]);
  }
}

export const routes: Routes = [{ path: '', component: BudgetsGlobalComponent }];
