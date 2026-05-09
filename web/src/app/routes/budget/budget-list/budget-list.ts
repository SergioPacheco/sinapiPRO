import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { MtxDialog } from '@ng-matero/extensions/dialog';
import { PageHeader } from '@shared';
import { BudgetService } from '../services/budget.service';
import { Budget } from '../models/budget.model';

@Component({
  selector: 'app-budget-list',
  templateUrl: './budget-list.html',
  imports: [MatButtonModule, MatIconModule, MatChipsModule, MtxGridModule, PageHeader],
})
export class BudgetListComponent implements OnInit {
  private readonly budgetService = inject(BudgetService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MtxDialog);

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'code', sortable: true, width: '120px' },
    { header: 'Título', field: 'title', sortable: true },
    { header: 'Cliente', field: 'customerName', sortable: true },
    {
      header: 'Valor Total',
      field: 'totalAmount',
      sortable: true,
      width: '150px',
      formatter: (data: Budget) => `R$ ${data.totalAmount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
    },
    {
      header: 'Status',
      field: 'status',
      width: '130px',
      formatter: (data: Budget) => {
        const labels: Record<string, string> = {
          ESTIMATE: 'Estimativa', SALE: 'Venda', EXECUTION: 'Execução', COMPLETED: 'Concluído',
        };
        return labels[data.status] ?? data.status;
      },
      tag: {
        ESTIMATE: { text: 'Estimativa', color: 'blue' },
        SALE: { text: 'Venda', color: 'orange' },
        EXECUTION: { text: 'Execução', color: 'green' },
        COMPLETED: { text: 'Concluído', color: 'default' },
      },
    },
    { header: 'Início', field: 'startDate', width: '120px' },
    {
      header: 'Ações',
      field: 'actions',
      width: '120px',
      pinned: 'right',
      type: 'button',
      buttons: [
        {
          type: 'icon',
          icon: 'edit',
          tooltip: 'Editar',
          click: (record: Budget) => this.router.navigate(['/budgets', record.id, 'edit']),
        },
        {
          type: 'icon',
          icon: 'delete',
          tooltip: 'Excluir',
          color: 'warn',
          click: (record: Budget) => this.confirmDelete(record),
        },
      ],
    },
  ];

  list: Budget[] = [];
  total = 0;
  isLoading = true;

  query = { page: 0, size: 20 };

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.budgetService.list(this.query.page, this.query.size).subscribe({
      next: res => {
        this.list = res.content;
        this.total = res.totalElements;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }

  confirmDelete(budget: Budget) {
    this.dialog.confirm(
      'Confirmar exclusão',
      `Deseja excluir o orçamento "${budget.code} - ${budget.title}"?`,
      () => this.budgetService.delete(budget.id).subscribe(() => this.loadData())
    );
  }

  create() {
    this.router.navigate(['/budgets/new']);
  }
}
