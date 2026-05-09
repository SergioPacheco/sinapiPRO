import { Component, inject, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { PageHeader } from '@shared';
import { AnalyticsService, EvmData } from '../services/analytics.service';

@Component({
  selector: 'app-analytics-list',
  templateUrl: './analytics-list.html',
  imports: [MatCardModule, MatIconModule, PageHeader],
})
export class AnalyticsListComponent implements OnInit {
  private readonly service = inject(AnalyticsService);

  evm: EvmData | null = null;
  isLoading = true;

  ngOnInit() {
    this.service.getEvm('default').subscribe({
      next: data => {
        this.evm = data;
        this.isLoading = false;
      },
      error: () => (this.isLoading = false),
    });
  }
}
