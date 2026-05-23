import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { SafetyService } from '../services/safety.service';
import { SafetyIncident } from '../models/safety.model';

@Component({
  selector: 'app-safety-list',
  templateUrl: './safety-list.html',
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class SafetyListComponent implements OnInit {
  private readonly service = inject(SafetyService);
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';

  columns: MtxGridColumn[] = [
    { header: 'Data', field: 'date', sortable: true, width: '120px' },
    { header: 'Descrição', field: 'description', sortable: true },
    {
      header: 'Severidade',
      field: 'severity',
      width: '130px',
      tag: {
        LOW: { text: 'Baixa', color: 'green' },
        MEDIUM: { text: 'Média', color: 'orange' },
        HIGH: { text: 'Alta', color: 'red' },
        CRITICAL: { text: 'Crítica', color: 'red' },
      },
    },
    {
      header: 'Resolvido',
      field: 'resolved',
      width: '100px',
      formatter: (data: SafetyIncident) => data.resolved ? 'Sim' : 'Não',
    },
  ];

  list: SafetyIncident[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() {
    let r = this.route.snapshot;
    while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.service.listTemplates(this.query.page, this.query.size).subscribe({
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

  downloadReport() {
    if (!this.projectId) return;
    this.http.get(`/projects/${this.projectId}/safety/reports/safety-report.pdf`, { responseType: 'blob' })
      .subscribe(blob => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = 'relatorio-seguranca.pdf';
        a.click();
        URL.revokeObjectURL(a.href);
      });
  }
}
