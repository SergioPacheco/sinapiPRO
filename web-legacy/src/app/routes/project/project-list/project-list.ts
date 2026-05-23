import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatBadgeModule } from '@angular/material/badge';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { ProjectService, Project } from '../services/project.service';

@Component({
  selector: 'app-project-list',
  templateUrl: './project-list.html',
  styleUrl: './project-list.scss',
  imports: [
    FormsModule, MatButtonModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatIconModule, MatProgressBarModule, MatBadgeModule,
    MtxGridModule, PageHeader,
  ],
})
export class ProjectListComponent implements OnInit {
  private readonly service = inject(ProjectService);
  readonly router = inject(Router);

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'code', width: '120px', sortable: true },
    { header: 'Obra', field: 'name', sortable: true },
    { header: 'Cliente', field: 'customerName', width: '200px' },
    { header: 'Status', field: 'status', width: '130px' },
    { header: 'Progresso', field: 'progress', width: '150px' },
    { header: 'Alertas', field: 'alertCount', width: '90px' },
    { header: 'Início', field: 'startDate', width: '110px' },
  ];

  list: Project[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };
  search = '';
  statusFilter = '';

  ngOnInit() { this.loadData(); }

  loadData() {
    this.isLoading = true;
    this.service.list(this.query.page, this.query.size, this.search || undefined, this.statusFilter || undefined).subscribe({
      next: res => { this.list = res.content; this.total = res.totalElements; this.isLoading = false; },
      error: () => { this.isLoading = false; },
    });
  }

  clampProgress(value: number | undefined): number {
    if (value == null) return 0;
    return Math.max(0, Math.min(100, value));
  }

  applyFilters() { this.query.page = 0; this.loadData(); }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }

  openProject(event: any) { this.router.navigate(['/projects', event.rowData.id]); }
  create() { this.router.navigate(['/projects/new']); }
}
