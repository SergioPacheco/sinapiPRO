import { ActivatedRoute } from '@angular/router';
import { Component, inject, OnInit } from '@angular/core';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { InventoryService } from '../services/inventory.service';
import { InventoryItem } from '../models/procurement.model';

@Component({
  selector: 'app-inventory-list',
  templateUrl: './inventory-list.html',
  imports: [MtxGridModule, PageHeader],
})
export class InventoryListComponent implements OnInit {
  private readonly service = inject(InventoryService);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';

  columns: MtxGridColumn[] = [
    { header: 'Descrição', field: 'description', sortable: true },
    { header: 'Qtd Atual', field: 'currentQuantity', width: '100px' },
    { header: 'Qtd Mínima', field: 'minQuantity', width: '110px' },
    { header: 'Unidade', field: 'unit', width: '80px' },
    { header: 'Local', field: 'location', width: '120px' },
    {
      header: 'Status',
      field: 'belowMinimum',
      width: '130px',
      tag: {
        true: { text: 'Abaixo do mínimo', color: 'red' },
        false: { text: 'Normal', color: 'green' },
      } as any,
    },
  ];

  list: InventoryItem[] = [];
  total = 0;
  isLoading = true;

  ngOnInit() {
    let r = this.route.snapshot;
    while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.service.listItems(this.projectId).subscribe({
      next: res => { this.list = res; this.total = res.length; this.isLoading = false; },
      error: () => (this.isLoading = false),
    });
  }
}
