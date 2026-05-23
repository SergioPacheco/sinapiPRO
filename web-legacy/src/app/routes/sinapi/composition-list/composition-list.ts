import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { SinapiService, CompositionFilters, FilterOptions } from '../services/sinapi.service';
import { Composition } from '../models/sinapi.model';

@Component({
  selector: 'app-composition-list',
  templateUrl: './composition-list.html',
  imports: [FormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatIconModule, MtxGridModule, PageHeader],
})
export class CompositionListComponent implements OnInit {
  private readonly service = inject(SinapiService);
  private readonly router = inject(Router);

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'sinapiCode', width: '120px', sortable: true },
    { header: 'Descrição', field: 'description', sortable: true },
    { header: 'Unidade', field: 'unit', width: '80px' },
    { header: 'Grupo', field: 'groupName', width: '180px' },
    { header: 'Origem', field: 'origin', width: '100px' },
  ];

  list: Composition[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  filters: CompositionFilters = {};
  filterOptions: FilterOptions = { units: [], origins: [], groups: [] };

  ngOnInit() {
    this.service.getCompositionFilters().subscribe(opts => this.filterOptions = opts);
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.service.listCompositions(this.query.page, this.query.size, this.filters).subscribe({
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

  onRowClick(row: any) { this.router.navigate(['/sinapi/compositions', row.rowData.id]); }
  create() { this.router.navigate(['/sinapi/compositions/new']); }

  get activeFilterCount(): number {
    return [this.filters.q, this.filters.origin, this.filters.unit, this.filters.groupName].filter(Boolean).length;
  }
}
