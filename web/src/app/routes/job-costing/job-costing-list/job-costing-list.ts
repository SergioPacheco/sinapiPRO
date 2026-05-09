import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { JobCostingService } from '../services/job-costing.service';
import { CostCode } from '../models/job-costing.model';

@Component({
  selector: 'app-job-costing-list',
  templateUrl: './job-costing-list.html',
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class JobCostingListComponent implements OnInit {
  private readonly service = inject(JobCostingService);

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'code', sortable: true, width: '120px' },
    { header: 'Descrição', field: 'description', sortable: true },
    {
      header: 'Orçado',
      field: 'budgetedAmount',
      width: '140px',
      formatter: (data: CostCode) =>
        `R$ ${data.budgetedAmount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
    },
    {
      header: 'Comprometido',
      field: 'committedAmount',
      width: '140px',
      formatter: (data: CostCode) =>
        `R$ ${data.committedAmount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
    },
    {
      header: 'Realizado',
      field: 'actualAmount',
      width: '140px',
      formatter: (data: CostCode) =>
        `R$ ${data.actualAmount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
    },
    {
      header: 'Variação',
      field: 'variance',
      width: '140px',
      formatter: (data: CostCode) => {
        const variance = data.budgetedAmount - data.actualAmount;
        return `R$ ${variance.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`;
      },
    },
  ];

  list: CostCode[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.service.listCostCodes(this.query.page, this.query.size).subscribe({
      next: res => {
        this.list = res.content;
        this.total = res.totalElements;
        this.isLoading = false;
      },
      error: () => (this.isLoading = false),
    });
  }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }
}
