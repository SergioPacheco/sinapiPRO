import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { CdkDragDrop, DragDropModule } from '@angular/cdk/drag-drop';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { ProjectService, Project } from '../services/project.service';

@Component({
  selector: 'app-project-list',
  templateUrl: './project-list.html',
  styleUrl: './project-list.scss',
  imports: [
    FormsModule, MatButtonModule, MatButtonToggleModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatIconModule, MatCardModule, DragDropModule, MtxGridModule, PageHeader,
  ],
})
export class ProjectListComponent implements OnInit {
  private readonly service = inject(ProjectService);
  readonly router = inject(Router);

  viewMode: 'table' | 'kanban' = 'kanban';

  kanbanColumns = [
    { status: 'PLANNING', label: 'Planejamento', color: '#2196f3' },
    { status: 'IN_PROGRESS', label: 'Em Execução', color: '#ff9800' },
    { status: 'SUSPENDED', label: 'Suspensa', color: '#9e9e9e' },
    { status: 'COMPLETED', label: 'Concluída', color: '#4caf50' },
  ];

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'code', width: '120px', sortable: true },
    { header: 'Obra', field: 'name', sortable: true },
    { header: 'Cliente', field: 'customerName', width: '200px' },
    { header: 'Cidade', field: 'city', width: '120px' },
    { header: 'UF', field: 'state', width: '50px' },
    { header: 'Status', field: 'status', width: '130px' },
    { header: 'Início', field: 'startDate', width: '110px' },
  ];

  list: Project[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 100 };
  search = '';
  statusFilter = '';

  ngOnInit() { this.loadData(); }

  loadData() {
    this.isLoading = true;
    const size = this.viewMode === 'kanban' ? 100 : this.query.size;
    this.service.list(this.query.page, size, this.search || undefined, this.statusFilter || undefined).subscribe({
      next: res => { this.list = res.content; this.total = res.totalElements; this.isLoading = false; },
      error: () => { this.isLoading = false; },
    });
  }

  getByStatus(status: string): Project[] {
    return this.list.filter(p => p.status === status);
  }

  get connectedLists(): string[] {
    return this.kanbanColumns.map(c => `kanban-${c.status}`);
  }

  onDrop(event: CdkDragDrop<string>) {
    if (event.previousContainer === event.container) return;
    const project = event.item.data as Project;
    const newStatus = event.container.data;
    this.service.updateStatus(project.id, newStatus).subscribe(() => {
      project.status = newStatus;
      this.list = [...this.list];
    });
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
