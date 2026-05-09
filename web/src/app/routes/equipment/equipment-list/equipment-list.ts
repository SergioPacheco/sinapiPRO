import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { EquipmentService } from '../services/equipment.service';
import { Equipment } from '../models/equipment.model';

@Component({
  selector: 'app-equipment-list',
  templateUrl: './equipment-list.html',
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class EquipmentListComponent implements OnInit {
  private readonly service = inject(EquipmentService);

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'code', sortable: true, width: '120px' },
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Tipo', field: 'type', width: '150px' },
    {
      header: 'Status',
      field: 'status',
      width: '130px',
      tag: {
        AVAILABLE: { text: 'Disponível', color: 'green' },
        IN_USE: { text: 'Em Uso', color: 'blue' },
        MAINTENANCE: { text: 'Manutenção', color: 'orange' },
      },
    },
    {
      header: 'Custo/Hora',
      field: 'hourlyRate',
      width: '130px',
      formatter: (data: Equipment) =>
        `R$ ${data.hourlyRate.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
    },
  ];

  list: Equipment[] = [];
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
