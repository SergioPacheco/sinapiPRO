import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { Routes } from '@angular/router';

@Component({
  selector: 'app-documents-list',
  template: `
    <page-header title="Documentos (GED)" subtitle="Gestão eletrônica de documentos da obra" />
    <mtx-grid [columns]="columns" [data]="list()" [length]="total()" [loading]="isLoading()"
      [pageOnFront]="false" [pageIndex]="page()" [pageSize]="20" [pageSizeOptions]="[10,20,50]"
      (page)="onPage($event)" [rowStriped]="true" />
  `,
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class DocumentsListComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';
  list = signal<any[]>([]);
  total = signal(0);
  isLoading = signal(true);
  page = signal(0);

  columns: MtxGridColumn[] = [
    { header: 'Título', field: 'title', sortable: true },
    { header: 'Arquivo', field: 'fileName', width: '200px' },
    { header: 'Tipo', field: 'contentType', width: '120px' },
    { header: 'Versão', field: 'version', width: '80px' },
    { header: 'Enviado por', field: 'uploadedBy', width: '140px' },
  ];

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.load();
  }

  load() {
    this.isLoading.set(true);
    this.http.get<any>(`/projects/${this.projectId}/documents`, { params: new HttpParams().set('page', this.page()).set('size', 20) })
      .subscribe({ next: r => { this.list.set(r.content); this.total.set(r.totalElements); this.isLoading.set(false); }, error: () => this.isLoading.set(false) });
  }

  onPage(e: any) { this.page.set(e.pageIndex); this.load(); }
}

export const routes: Routes = [{ path: '', component: DocumentsListComponent }];
