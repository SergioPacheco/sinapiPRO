import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { Routes } from '@angular/router';

interface Submittal {
  id: string; number: number; title: string; type: string;
  status: string; submittedBy: string; assignedTo: string; dueDate: string;
}

@Component({
  selector: 'app-submittal-list',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
  template: `
    <page-header title="Submittals" subtitle="Controle de envio e aprovação de documentos técnicos">
      <button mat-flat-button color="primary" (click)="create()"><mat-icon>add</mat-icon> Novo Submittal</button>
    </page-header>
    <mtx-grid [columns]="columns" [data]="list()" [loading]="loading()"
              [pageOnFront]="true" [pageSize]="20" />
  `,
})
export class SubmittalListComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';

  list = signal<Submittal[]>([]);
  loading = signal(true);

  columns: MtxGridColumn[] = [
    { header: '#', field: 'number', width: '60px', sortable: true },
    { header: 'Título', field: 'title', sortable: true },
    { header: 'Tipo', field: 'type', width: '120px' },
    { header: 'Enviado por', field: 'submittedBy', width: '150px' },
    { header: 'Responsável', field: 'assignedTo', width: '150px' },
    { header: 'Prazo', field: 'dueDate', width: '110px' },
    {
      header: 'Status', field: 'status', width: '140px',
      tag: {
        DRAFT: { text: 'Rascunho', color: 'default' },
        SUBMITTED: { text: 'Enviado', color: 'blue' },
        UNDER_REVIEW: { text: 'Em Análise', color: 'orange' },
        APPROVED: { text: 'Aprovado', color: 'green' },
        APPROVED_AS_NOTED: { text: 'Aprovado c/ Ressalvas', color: 'cyan' },
        REJECTED: { text: 'Rejeitado', color: 'red' },
        REVISED: { text: 'Revisado', color: 'purple' },
      },
    },
  ];

  ngOnInit() {
    let r = this.route.snapshot;
    while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.http.get<any>(`/projects/${this.projectId}/submittals`).subscribe({
      next: res => { this.list.set(res.content || res || []); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  create() { /* TODO: open form */ }
}

export const routes: Routes = [{ path: '', component: SubmittalListComponent }];
