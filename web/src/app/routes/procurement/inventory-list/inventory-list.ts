import { ActivatedRoute } from '@angular/router';
import { Component, inject, OnInit } from '@angular/core';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { ProcurementService } from '../services/procurement.service';
import { InventoryItem } from '../models/procurement.model';

@Component({
  selector: 'app-inventory-list',
  templateUrl: './inventory-list.html',
  imports: [MtxGridModule, PageHeader],
})
export class InventoryListComponent implements OnInit {
  private readonly service = inject(ProcurementService);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'materialCode', width: '120px', sortable: true },
    { header: 'Descrição', field: 'description', sortable: true },
    { header: 'Qtd', field: 'quantity', width: '80px' },
    { header: 'Unidade', field: 'unit', width: '80px' },
    { header: 'Custo Médio', field: 'averageCost', width: '130px', formatter: (d: InventoryItem) => `R$ ${d.averageCost.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}` },
    { header: 'Valor Total', field: 'totalValue', width: '130px', formatter: (d: InventoryItem) => `R$ ${d.totalValue.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}` },
  ];

  list: InventoryItem[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get("projectId")) r = r.parent; this.projectId = r.paramMap.get("projectId") || ""; this.projectId = this.route.parent!.parent!.snapshot.paramMap.get('projectId')!; this.loadData(); }

  loadData() {
    this.isLoading = true;
    this.service.listInventory(this.projectId, this.query.page, this.query.size).subscribe({
      next: res => { this.list = res.content; this.total = res.totalElements; this.isLoading = false; },
      error: () => (this.isLoading = false),
    });
  }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }
}
