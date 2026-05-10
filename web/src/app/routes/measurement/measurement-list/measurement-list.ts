import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatRippleModule } from '@angular/material/core';
import { MtxDialog } from '@ng-matero/extensions/dialog';
import { PageHeader } from '@shared';
import { MeasurementService } from '../services/measurement.service';
import { Measurement } from '../models/measurement.model';

@Component({
  selector: 'app-measurement-list',
  template: `
    <page-header title="Medições" subtitle="Workflow de medições da obra">
      <button mat-flat-button color="primary" (click)="create()"><mat-icon>add</mat-icon> Nova Medição</button>
    </page-header>

    <div class="kanban-board">
      @for (col of columns; track col.status) {
        <div class="kanban-column">
          <div class="kanban-header" [style.border-top-color]="col.color">
            <span class="kanban-title">{{ col.label }}</span>
            <span class="kanban-count">{{ getByStatus(col.status).length }}</span>
          </div>
          <div class="kanban-cards">
            @for (m of getByStatus(col.status); track m.id) {
              <mat-card class="kanban-card" matRipple>
                <div class="card-header">
                  <strong>Medição #{{ m.number }}</strong>
                  <span class="card-amount">{{ formatCurrency(m.grossAmount) }}</span>
                </div>
                <p class="card-period">{{ m.periodStart }} → {{ m.periodEnd }}</p>
                <div class="card-actions">
                  @if (m.status === 'DRAFT') {
                    <button mat-icon-button color="primary" (click)="submit(m)" matTooltip="Submeter">
                      <mat-icon>send</mat-icon>
                    </button>
                  }
                  @if (m.status === 'SUBMITTED') {
                    <button mat-icon-button color="accent" (click)="approve(m)" matTooltip="Aprovar">
                      <mat-icon>check_circle</mat-icon>
                    </button>
                  }
                  <button mat-icon-button (click)="viewReport(m)" matTooltip="Boletim PDF">
                    <mat-icon>picture_as_pdf</mat-icon>
                  </button>
                </div>
              </mat-card>
            }
            @if (getByStatus(col.status).length === 0) {
              <div class="kanban-empty">Nenhuma medição</div>
            }
          </div>
        </div>
      }
    </div>
  `,
  styles: `
    .kanban-board { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; min-height: 400px; }
    @media (max-width: 1200px) { .kanban-board { grid-template-columns: repeat(2, 1fr); } }
    .kanban-column { background: var(--mat-sys-surface-container-low); border-radius: 12px; padding: 12px; }
    .kanban-header { border-top: 3px solid; padding: 8px 4px 12px; display: flex; justify-content: space-between; align-items: center; }
    .kanban-title { font-weight: 600; font-size: 14px; }
    .kanban-count { background: var(--mat-sys-surface-container-highest); border-radius: 12px; padding: 2px 8px; font-size: 12px; font-weight: 600; }
    .kanban-cards { display: flex; flex-direction: column; gap: 8px; }
    .kanban-card { padding: 12px; cursor: pointer; transition: transform 0.15s; }
    .kanban-card:hover { transform: translateY(-1px); }
    .card-header { display: flex; justify-content: space-between; align-items: center; }
    .card-amount { font-size: 13px; color: #4caf50; font-weight: 600; }
    .card-period { font-size: 12px; color: var(--mat-sys-on-surface-variant); margin: 4px 0 0; }
    .card-actions { display: flex; gap: 4px; margin-top: 8px; }
    .kanban-empty { text-align: center; padding: 24px; color: var(--mat-sys-on-surface-variant); font-size: 13px; }
  `,
  imports: [MatButtonModule, MatIconModule, MatCardModule, MatChipsModule, MatRippleModule, PageHeader],
})
export class MeasurementListComponent implements OnInit {
  private readonly service = inject(MeasurementService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly dialog = inject(MtxDialog);
  private projectId = '';

  list = signal<Measurement[]>([]);

  columns = [
    { status: 'DRAFT', label: 'Rascunho', color: '#2196f3' },
    { status: 'SUBMITTED', label: 'Enviada', color: '#ff9800' },
    { status: 'APPROVED', label: 'Aprovada', color: '#4caf50' },
    { status: 'PAID', label: 'Paga', color: '#9e9e9e' },
  ];

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.loadData();
  }

  loadData() {
    this.service.list(this.projectId, 0, 100).subscribe(res => this.list.set(res.content));
  }

  getByStatus(status: string) { return this.list().filter(m => m.status === status); }

  formatCurrency(v: number) { return `R$ ${(v ?? 0).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`; }

  submit(m: Measurement) {
    this.dialog.confirm('Submeter medição', `Submeter medição #${m.number} para aprovação?`, () =>
      this.service.submit(this.projectId, m.id).subscribe(() => this.loadData()));
  }

  approve(m: Measurement) {
    this.dialog.confirm('Aprovar medição', `Aprovar medição #${m.number}?`, () =>
      this.service.approve(this.projectId, m.id).subscribe(() => this.loadData()));
  }

  viewReport(m: Measurement) { window.open(this.service.bulletinReportUrl(this.projectId, m.id), '_blank'); }

  create() { this.router.navigate(['new'], { relativeTo: this.route }); }
}
