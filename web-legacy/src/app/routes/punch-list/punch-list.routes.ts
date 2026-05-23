import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { Routes } from '@angular/router';

@Component({
  selector: 'app-punch-list',
  template: `
    <page-header title="Punch List" subtitle="Pendências e não-conformidades" />
    <mtx-grid [columns]="columns" [data]="list()" [length]="total()" [loading]="isLoading()"
      [pageOnFront]="false" [pageIndex]="page()" [pageSize]="20" [pageSizeOptions]="[10,20,50]"
      (page)="onPage($event)" [rowStriped]="true" />
  `,
  imports: [MatIconModule, MtxGridModule, PageHeader],
})
export class PunchListComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';
  list = signal<any[]>([]);
  total = signal(0);
  isLoading = signal(true);
  page = signal(0);

  columns: MtxGridColumn[] = [
    { header: '#', field: 'number', width: '60px' },
    { header: 'Descrição', field: 'description', sortable: true },
    { header: 'Local', field: 'location', width: '140px' },
    { header: 'Responsável', field: 'assignedTo', width: '140px' },
    { header: 'Prazo', field: 'dueDate', width: '110px' },
    { header: 'Prioridade', field: 'priority', width: '100px', tag: { LOW: { text: 'Baixa', color: '' }, MEDIUM: { text: 'Média', color: 'orange' }, HIGH: { text: 'Alta', color: 'red' } } },
    { header: 'Status', field: 'status', width: '120px', tag: { OPEN: { text: 'Aberto', color: 'orange' }, IN_PROGRESS: { text: 'Em Andamento', color: 'blue' }, RESOLVED: { text: 'Resolvido', color: 'green' }, CLOSED: { text: 'Fechado', color: '' } } },
  ];

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.load();
  }

  load() {
    this.isLoading.set(true);
    this.http.get<any>(`/projects/${this.projectId}/punch-list`, { params: new HttpParams().set('page', this.page()).set('size', 20) })
      .subscribe({ next: r => { this.list.set(r.content); this.total.set(r.totalElements); this.isLoading.set(false); }, error: () => this.isLoading.set(false) });
  }

  onPage(e: any) { this.page.set(e.pageIndex); this.load(); }
}

export const routes: Routes = [{ path: '', component: PunchListComponent }];
