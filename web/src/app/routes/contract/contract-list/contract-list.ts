import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { ContractService } from '../services/contract.service';
import { Contract } from '../models/contract.model';

@Component({
  selector: 'app-contract-list',
  templateUrl: './contract-list.html',
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class ContractListComponent implements OnInit {
  private readonly service = inject(ContractService);

  columns: MtxGridColumn[] = [
    { header: 'Número', field: 'number', sortable: true, width: '120px' },
    { header: 'Descrição', field: 'description', sortable: true },
    {
      header: 'Valor Original',
      field: 'originalValue',
      width: '150px',
      formatter: (data: Contract) =>
        `R$ ${data.originalValue.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
    },
    {
      header: 'Status',
      field: 'status',
      width: '130px',
      tag: {
        DRAFT: { text: 'Rascunho', color: 'blue' },
        ACTIVE: { text: 'Ativo', color: 'green' },
        COMPLETED: { text: 'Concluído', color: 'default' },
        CANCELLED: { text: 'Cancelado', color: 'red' },
      },
    },
    { header: 'Início', field: 'startDate', width: '120px' },
  ];

  list: Contract[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.service.list(this.query.page, this.query.size).subscribe({
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
