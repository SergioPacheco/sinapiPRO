import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { SinapiService, MaterialFilters, FilterOptions } from '../services/sinapi.service';
import { Material } from '../models/sinapi.model';

@Component({
  selector: 'app-material-list',
  templateUrl: './material-list.html',
  imports: [FormsModule, MatButtonModule, MatChipsModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatIconModule, MtxGridModule, PageHeader],
})
export class MaterialListComponent implements OnInit {
  private readonly service = inject(SinapiService);
  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'sinapiCode', width: '120px', sortable: true },
    { header: 'Descrição', field: 'description', sortable: true },
    { header: 'Unidade', field: 'unit', width: '80px' },
    { header: 'Origem', field: 'origin', width: '100px' },
    { header: 'Preço (R$)', field: 'price', width: '120px' },
  ];

  list: Material[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  // Filters
  filters: MaterialFilters = {};
  filterOptions: FilterOptions = { units: [], origins: [] };

  ngOnInit() {
    this.service.getMaterialFilters().subscribe(opts => this.filterOptions = opts);
    // Load global settings for default price reference
    this.http.get<any>('/settings').subscribe(s => {
      this.filters.state = s.state;
      this.filters.referenceMonth = s.referenceMonth;
      this.loadData();
    }, () => this.loadData());
  }

  loadData() {
    this.isLoading = true;
    this.service.listMaterials(this.query.page, this.query.size, this.filters).subscribe({
      next: res => { this.list = res.content; this.total = res.totalElements; this.isLoading = false; },
      error: () => { this.isLoading = false; },
    });
  }

  applyFilters() { this.query.page = 0; this.loadData(); }
  clearFilters() { this.filters = {}; this.applyFilters(); }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }

  onRowClick(row: any) { this.router.navigate(['/sinapi/materials', row.rowData.id]); }
  create() { this.router.navigate(['/sinapi/materials/new']); }

  get activeFilterCount(): number {
    return [this.filters.q, this.filters.origin, this.filters.unit, this.filters.state].filter(Boolean).length;
  }
}
