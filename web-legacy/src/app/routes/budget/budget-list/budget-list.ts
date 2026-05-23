import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
  private readonly route = inject(ActivatedRoute);
  private projectId = '';
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
          DRAFT: 'Rascunho',
          IN_REVIEW: 'Em análise',
          APPROVED: 'Aprovado',
          REJECTED: 'Reprovado',
          SUPERSEDED: 'Substituído',
          IN_EXECUTION: 'Em execução',
          COMPLETED: 'Concluído',
          CANCELLED: 'Cancelado',
        };
        return labels[data.status] ?? data.status;
      },
      tag: {
        DRAFT: { text: 'Rascunho', color: 'default' },
        IN_REVIEW: { text: 'Em análise', color: 'blue' },
        APPROVED: { text: 'Aprovado', color: 'green' },
        REJECTED: { text: 'Reprovado', color: 'red' },
        SUPERSEDED: { text: 'Substituído', color: 'orange' },
        IN_EXECUTION: { text: 'Em execução', color: 'green' },
        COMPLETED: { text: 'Concluído', color: 'default' },
        CANCELLED: { text: 'Cancelado', color: 'red' },
      },
    },
    { header: 'Início', field: 'startDate', width: '120px' },
    {
      header: 'Ações',
      field: 'actions',
      width: '180px',
      pinned: 'right',
      type: 'button',
      buttons: [
        {
          type: 'icon',
          icon: 'edit',
          tooltip: 'Editar',
          click: (record: Budget) => this.router.navigate([record.id, 'edit'], { relativeTo: this.route }),
        },
        {
          type: 'icon',
          icon: 'content_copy',
          tooltip: 'Copiar orçamento',
          click: (record: Budget) => this.copyBudget(record),
        },
        {
          type: 'icon',
          icon: 'play_circle',
          tooltip: 'Efetivar orçamento',
          click: (record: Budget) => this.activateBudget(record),
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
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get('projectId')) r = r.parent; this.projectId = r.paramMap.get('projectId') || '';
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.budgetService.list(this.projectId, this.query.page, this.query.size).subscribe({
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
      () => this.budgetService.delete(this.projectId, budget.id).subscribe(() => this.loadData())
    );
  }

  copyBudget(budget: Budget) {
    const code = prompt('Código do novo orçamento', `${budget.code}-COPIA`);
    if (!code) return;
    const title = prompt('Título do novo orçamento', `${budget.title} - Cópia`);
    if (!title) return;
    this.budgetService.copy(this.projectId, budget.id, { code, title }).subscribe(() => this.loadData());
  }

  activateBudget(budget: Budget) {
    this.dialog.confirm(
      'Efetivar orçamento',
      `Deseja efetivar "${budget.code} - ${budget.title}" como orçamento vigente?`,
      () => this.budgetService.activate(this.projectId, budget.id).subscribe(() => this.loadData())
    );
  }

  create() {
    this.router.navigate(['new'], { relativeTo: this.route });
  }

  openWorkspace(event: any) {
    this.router.navigate([event.rowData.id, 'worksheet'], { relativeTo: this.route });
  }
}
