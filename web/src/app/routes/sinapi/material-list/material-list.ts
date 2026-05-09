import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { SinapiService } from '../services/sinapi.service';
import { Material } from '../models/sinapi.model';

@Component({
  selector: 'app-material-list',
  templateUrl: './material-list.html',
  imports: [FormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatIconModule, MtxGridModule, PageHeader],
})
export class MaterialListComponent implements OnInit {
  private readonly service = inject(SinapiService);

  columns: MtxGridColumn[] = [
    { header: 'Código SINAPI', field: 'sinapiCode', width: '140px', sortable: true },
    { header: 'Descrição', field: 'description', sortable: true },
    { header: 'Unidade', field: 'unit', width: '80px' },
    { header: 'Origem', field: 'origin', width: '120px' },
  ];

  list: Material[] = [];
  total = 0;
  isLoading = true;
  search = '';
  query = { page: 0, size: 20 };

  ngOnInit() { this.loadData(); }

  loadData() {
    this.isLoading = true;
    this.service.listMaterials(this.query.page, this.query.size, this.search || undefined).subscribe({
      next: res => { this.list = res.content; this.total = res.totalElements; this.isLoading = false; },
      error: () => { this.isLoading = false; },
    });
  }

  onSearch() { this.query.page = 0; this.loadData(); }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }
}
