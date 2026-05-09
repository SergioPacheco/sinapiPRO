import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-safety-list',
  standalone: true,
  imports: [MatCardModule, MatIconModule, MatButtonModule, PageHeader],
  template: `
    <page-header title="Safety" />
    <mat-card>
      <mat-card-content>
        <p>Módulo Safety — em desenvolvimento</p>
      </mat-card-content>
    </mat-card>
  `,
})
export class SafetyListComponent {}
