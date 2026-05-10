import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
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
  private readonly route = inject(ActivatedRoute);
  private readonly http = inject(HttpClient);
  private projectId = '';

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
    {
      header: '',
      field: 'actions',
      width: '60px',
      type: 'button',
      buttons: [
        {
          type: 'icon',
          icon: 'picture_as_pdf',
          tooltip: 'Contrato PDF',
          click: (record: Contract) => this.downloadPdf(record),
        },
      ],
    },
  ];

  list: Contract[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get("projectId")) r = r.parent; this.projectId = r.paramMap.get("projectId") || "";
    this.projectId = this.route.parent!.parent!.snapshot.paramMap.get('projectId')!;
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.service.list(this.projectId, this.query.page, this.query.size).subscribe({
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

  downloadPdf(contract: Contract) {
    this.http.get(`/projects/${this.projectId}/contracts/${contract.id}/reports/contract.pdf`, { responseType: 'blob' })
      .subscribe(blob => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `contrato-${contract.number}.pdf`;
        a.click();
        URL.revokeObjectURL(a.href);
      });
  }
}
