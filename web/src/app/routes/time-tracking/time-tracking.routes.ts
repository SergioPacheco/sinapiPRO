import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { Routes } from '@angular/router';

@Component({
  selector: 'app-time-tracking-list',
  template: `
    <page-header title="Apontamento de Horas" subtitle="Controle de horas trabalhadas por funcionário" />
    <mtx-grid [columns]="columns" [data]="list()" [length]="total()" [loading]="isLoading()"
      [pageOnFront]="false" [pageIndex]="page()" [pageSize]="20" [pageSizeOptions]="[10,20,50]"
      (page)="onPage($event)" [rowStriped]="true" />
  `,
  imports: [MatIconModule, MtxGridModule, PageHeader],
})
export class TimeTrackingListComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';
  list = signal<any[]>([]);
  total = signal(0);
  isLoading = signal(true);
  page = signal(0);

  columns: MtxGridColumn[] = [
    { header: 'Funcionário', field: 'workerName', sortable: true },
    { header: 'Data', field: 'workDate', width: '110px' },
    { header: 'Entrada', field: 'startTime', width: '90px' },
    { header: 'Saída', field: 'endTime', width: '90px' },
    { header: 'Horas', field: 'totalHours', width: '80px' },
    { header: 'Atividade', field: 'activity', width: '200px' },
    { header: 'Status', field: 'status', width: '110px', tag: { PENDING: { text: 'Pendente', color: 'orange' }, APPROVED: { text: 'Aprovado', color: 'green' } } },
  ];

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.load();
  }

  load() {
    this.isLoading.set(true);
    this.http.get<any>(`/projects/${this.projectId}/time-tracking`, { params: new HttpParams().set('page', this.page()).set('size', 20) })
      .subscribe({ next: r => { this.list.set(r.content); this.total.set(r.totalElements); this.isLoading.set(false); }, error: () => this.isLoading.set(false) });
  }

  onPage(e: any) { this.page.set(e.pageIndex); this.load(); }
}

export const routes: Routes = [{ path: '', component: TimeTrackingListComponent }];
