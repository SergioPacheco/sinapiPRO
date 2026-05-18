import { ActivatedRoute } from '@angular/router';
import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { DailyLogService } from '../services/daily-log.service';
import { DailyLog } from '../models/daily-log.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-daily-log-list',
  templateUrl: './daily-log-list.html',
  imports: [MatButtonModule, MatCardModule, MatIconModule, MtxGridModule, PageHeader],
})
export class DailyLogListComponent implements OnInit {
  private readonly service = inject(DailyLogService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private projectId = '';

  columns: MtxGridColumn[] = [
    { header: 'Data', field: 'logDate', sortable: true, width: '120px' },
    { header: 'Clima manhã', field: 'weatherMorning', width: '120px' },
    { header: 'Clima tarde', field: 'weatherAfternoon', width: '120px' },
    { header: 'Mão de Obra', field: 'laborCount', width: '120px' },
    { header: 'Equipamentos', field: 'equipmentCount', width: '130px' },
    { header: 'Ocorrências', field: 'occurrenceCount', width: '120px' },
    { header: 'Fotos', field: 'photoCount', width: '90px' },
    {
      header: 'Observações',
      field: 'observations',
      formatter: (data: DailyLog) => {
        const observations = data.observations ?? '';
        return observations.length > 50 ? observations.substring(0, 50) + '...' : observations;
      },
    },
    {
      header: '',
      field: 'actions',
      width: '120px',
      type: 'button',
      buttons: [
        {
          type: 'icon',
          icon: 'picture_as_pdf',
          tooltip: 'RDO PDF',
          click: (record: DailyLog) => this.downloadRdo(record),
        },
        {
          type: 'icon',
          icon: 'open_in_new',
          tooltip: 'Detalhar',
          click: (record: DailyLog) => this.openDetail(record),
        },
      ],
    },
  ];

  list: DailyLog[] = [];
  weatherSummary: { totalDelays: number; fullDaysLost: number; totalHoursLost: number } | null = null;
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() {
    let r = this.route.snapshot;
    while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.loadData();
    this.service.weatherDelaySummary(this.projectId).subscribe(res => this.weatherSummary = res);
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
    window.open(this.service.rdoReportUrl(this.projectId, log.id), '_blank');
  }

  openDetail(log: DailyLog) {
    this.router.navigate([log.id], { relativeTo: this.route });
  }
}
