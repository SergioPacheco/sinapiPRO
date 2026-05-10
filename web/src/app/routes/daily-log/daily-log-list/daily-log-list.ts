import { ActivatedRoute } from '@angular/router';
import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { DailyLogService } from '../services/daily-log.service';
import { DailyLog } from '../models/daily-log.model';

@Component({
  selector: 'app-daily-log-list',
  templateUrl: './daily-log-list.html',
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class DailyLogListComponent implements OnInit {
  private readonly service = inject(DailyLogService);
  private readonly route = inject(ActivatedRoute);
  private readonly http = inject(HttpClient);
  private projectId = '';

  columns: MtxGridColumn[] = [
    { header: 'Data', field: 'date', sortable: true, width: '120px' },
    { header: 'Clima', field: 'weather', width: '120px' },
    { header: 'Temp. (°C)', field: 'temperature', width: '100px' },
    { header: 'Mão de Obra', field: 'laborCount', width: '120px' },
    { header: 'Equipamentos', field: 'equipmentCount', width: '130px' },
    {
      header: 'Observações',
      field: 'notes',
      formatter: (data: DailyLog) =>
        data.notes?.length > 50 ? data.notes.substring(0, 50) + '...' : (data.notes ?? ''),
    },
    {
      header: '',
      field: 'actions',
      width: '60px',
      type: 'button',
      buttons: [
        {
          type: 'icon',
          icon: 'picture_as_pdf',
          tooltip: 'RDO PDF',
          click: (record: DailyLog) => this.downloadRdo(record),
        },
      ],
    },
  ];

  list: DailyLog[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get("projectId")) r = r.parent; this.projectId = r.paramMap.get("projectId") || "";
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.service.list(this.projectId, this.query.page, this.query.size).subscribe({
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

  downloadRdo(log: DailyLog) {
    this.http.get(`/projects/${this.projectId}/daily-logs/${log.id}/reports/rdo.pdf`, { responseType: 'blob' })
      .subscribe(blob => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `rdo-${log.date}.pdf`;
        a.click();
        URL.revokeObjectURL(a.href);
      });
  }
}
