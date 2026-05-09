import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { ScheduleService } from '../services/schedule.service';
import { ScheduleActivity } from '../models/schedule.model';

@Component({
  selector: 'app-schedule-list',
  templateUrl: './schedule-list.html',
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class ScheduleListComponent implements OnInit {
  private readonly service = inject(ScheduleService);

  columns: MtxGridColumn[] = [
    { header: 'Atividade', field: 'name', sortable: true },
    { header: 'Início', field: 'startDate', width: '120px' },
    { header: 'Fim', field: 'endDate', width: '120px' },
    { header: 'Duração (dias)', field: 'duration', width: '120px' },
    {
      header: 'Progresso',
      field: 'progress',
      width: '110px',
      formatter: (data: ScheduleActivity) => `${data.progress}%`,
    },
    {
      header: 'Crítico',
      field: 'isCritical',
      width: '100px',
      formatter: (data: ScheduleActivity) => data.isCritical ? 'Sim' : 'Não',
    },
  ];

  list: ScheduleActivity[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.isLoading = true;
    this.service.listActivities(this.query.page, this.query.size).subscribe({
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
}
