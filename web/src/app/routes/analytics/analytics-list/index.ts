import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-analytics-list',
  standalone: true,
  imports: [MatCardModule, MatIconModule, MatButtonModule, PageHeader],
  template: `
    <page-header title="Analytics" />
    <mat-card>
      <mat-card-content>
        <p>Módulo Analytics — em desenvolvimento</p>
      </mat-card-content>
    </mat-card>
  `,
})
export class AnalyticsListComponent {}
