import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-sinapi-list',
  standalone: true,
  imports: [MatCardModule, MatIconModule, MatButtonModule, PageHeader],
  template: `
    <page-header title="Sinapi" />
    <mat-card>
      <mat-card-content>
        <p>Módulo Sinapi — em desenvolvimento</p>
      </mat-card-content>
    </mat-card>
  `,
})
export class SinapiListComponent {}
