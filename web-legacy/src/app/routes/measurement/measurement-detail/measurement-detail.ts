import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatListModule } from '@angular/material/list';
import { MtxDialog } from '@ng-matero/extensions/dialog';
import { PageHeader } from '@shared';
import { MeasurementService } from '../services/measurement.service';
import { MeasurementDetail, MeasurementHistoryEntry } from '../models/measurement.model';
import { MeasurementMemoDialogComponent } from '../dialogs/measurement-memo-dialog';
import { ExtraItemDialogComponent } from '../dialogs/extra-item-dialog';
import { ExtraMeasurementItemResult } from '../dialogs/extra-item-dialog';
import { RejectMeasurementDialogComponent } from '../dialogs/reject-measurement-dialog';

@Component({
  selector: 'app-measurement-detail',
  template: `
    @if (detail()) {
      <page-header title="Detalhe da Medição" [subtitle]="'Medição #' + detail()!.number">
        <button mat-stroked-button (click)="back()"><mat-icon>arrow_back</mat-icon> Voltar</button>
        @if (detail()!.status === 'SUBMITTED') {
          <button mat-flat-button color="primary" (click)="approve()"><mat-icon>check_circle</mat-icon> Aprovar</button>
          <button mat-flat-button color="warn" (click)="reject()"><mat-icon>cancel</mat-icon> Rejeitar</button>
        }
        <button mat-flat-button color="primary" (click)="addMemo()"><mat-icon>calculate</mat-icon> Memória</button>
        <button mat-flat-button color="accent" (click)="addExtraItem()"><mat-icon>add_circle</mat-icon> Serviço extra</button>
      </page-header>

      <div class="summary-grid">
        <mat-card><strong>Bruto</strong><span>{{ detail()!.grossAmount | number:'1.2-2' }}</span></mat-card>
        <mat-card><strong>Líquido</strong><span>{{ detail()!.netAmount | number:'1.2-2' }}</span></mat-card>
        <mat-card><strong>Status</strong><span>{{ detail()!.status }}</span></mat-card>
        <mat-card><strong>Retenção</strong><span>{{ detail()!.retentionPct | number:'1.2-4' }}</span></mat-card>
      </div>

      <div class="summary-grid">
        @if (lastMemo()) {
          <mat-card>
            <strong>Última memória</strong>
            <span>{{ lastMemo()!.description }}</span>
            <small>{{ lastMemo()!.formula }} = {{ lastMemo()!.value }}</small>
          </mat-card>
        }
        @if (lastExtra()) {
          <mat-card>
            <strong>Último extra</strong>
            <span>{{ lastExtra()!.description }}</span>
            <small>{{ lastExtra()!.quantity }} x {{ lastExtra()!.unitPrice }}</small>
          </mat-card>
        }
      </div>

      <mat-card>
        <mat-card-title>Memória de cálculo</mat-card-title>
        <table mat-table [dataSource]="detail()!.items" class="detail-table">
          <ng-container matColumnDef="description">
            <th mat-header-cell *matHeaderCellDef>Descrição</th>
            <td mat-cell *matCellDef="let row">{{ row.description }}</td>
          </ng-container>
          <ng-container matColumnDef="periodQuantity">
            <th mat-header-cell *matHeaderCellDef>Qtd. período</th>
            <td mat-cell *matCellDef="let row">{{ row.periodQuantity | number:'1.2-4' }}</td>
          </ng-container>
          <ng-container matColumnDef="previousQuantity">
            <th mat-header-cell *matHeaderCellDef>Anterior</th>
            <td mat-cell *matCellDef="let row">{{ row.previousQuantity | number:'1.2-4' }}</td>
          </ng-container>
          <ng-container matColumnDef="cumulativeQuantity">
            <th mat-header-cell *matHeaderCellDef>Acumulado</th>
            <td mat-cell *matCellDef="let row">{{ row.cumulativeQuantity | number:'1.2-4' }}</td>
          </ng-container>
          <ng-container matColumnDef="balanceQuantity">
            <th mat-header-cell *matHeaderCellDef>Saldo</th>
            <td mat-cell *matCellDef="let row">{{ row.balanceQuantity | number:'1.2-4' }}</td>
          </ng-container>
          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef></th>
            <td mat-cell *matCellDef="let row">
              <button mat-icon-button color="primary" matTooltip="Memória do item" (click)="addMemo(row.id)">
                <mat-icon>calculate</mat-icon>
              </button>
            </td>
          </ng-container>
          <tr mat-header-row *matHeaderRowDef="columns"></tr>
          <tr mat-row *matRowDef="let row; columns: columns;"></tr>
        </table>
      </mat-card>

      <mat-card class="history-card">
        <mat-card-title>Histórico de aprovação</mat-card-title>
        @if (history().length > 0) {
          <mat-list>
            @for (event of history(); track event.id) {
              <mat-list-item>
                <span matListItemTitle>{{ event.action }}: {{ event.fromStatus || '-' }} → {{ event.toStatus }}</span>
                <span matListItemLine>{{ event.performedBy || 'Sistema' }} @ {{ event.createdAt || '-' }}</span>
                @if (event.reason) {
                  <span matListItemLine>Motivo: {{ event.reason }}</span>
                }
              </mat-list-item>
            }
          </mat-list>
        } @else {
          <p class="empty-history">Sem eventos registrados.</p>
        }
      </mat-card>
    }
  `,
  styles: `
    .summary-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 16px; }
    .summary-grid mat-card { padding: 16px; display: grid; gap: 8px; }
    .summary-grid span { font-size: 22px; font-weight: 700; }
    .detail-table { width: 100%; }
    .history-card { margin-top: 16px; }
    .empty-history { margin: 12px 0; color: var(--mat-sys-on-surface-variant); }
  `,
  imports: [DecimalPipe, MatButtonModule, MatCardModule, MatIconModule, MatTableModule, MatTooltipModule, MatListModule, PageHeader],
})
export class MeasurementDetailComponent implements OnInit {
  private readonly service = inject(MeasurementService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly confirm = inject(MtxDialog);
  private readonly projectId = this.route.parent?.snapshot.paramMap.get('projectId') || '';
  private readonly measurementId = this.route.snapshot.paramMap.get('id') || '';

  detail = signal<MeasurementDetail | null>(null);
  history = signal<MeasurementHistoryEntry[]>([]);
  lastMemo = signal<{ description: string; formula: string; value: number } | null>(null);
  lastExtra = signal<ExtraMeasurementItemResult | null>(null);
  columns = ['description', 'periodQuantity', 'previousQuantity', 'cumulativeQuantity', 'balanceQuantity', 'actions'];

  ngOnInit() {
    this.reloadDetail();
  }

  back() {
    this.router.navigate(['../'], { relativeTo: this.route });
  }

  addMemo(itemId?: string) {
    const targetItemId = itemId || this.detail()?.items[0]?.id;
    if (!targetItemId) return;
    this.service.getMemo(this.projectId, this.measurementId, targetItemId).subscribe({
      next: memo => this.openMemoDialog(targetItemId, memo.lines),
      error: () => this.openMemoDialog(targetItemId, []),
    });
  }

  addExtraItem() {
    const ref = this.dialog.open(ExtraItemDialogComponent, { width: '520px' });
    ref.afterClosed().subscribe(result => {
      if (!result) return;
      this.service.addExtraItem(this.projectId, this.measurementId, result).subscribe(() => {
        this.lastExtra.set(result);
        this.reloadDetail();
      });
    });
  }

  approve() {
    this.confirm.confirm('Aprovar medição', 'Confirmar aprovação desta medição?', () => {
      this.service.approve(this.projectId, this.measurementId).subscribe(() => this.reloadDetail());
    });
  }

  reject() {
    const ref = this.dialog.open(RejectMeasurementDialogComponent, { width: '480px' });
    ref.afterClosed().subscribe((reason?: string) => {
      if (!reason) return;
      this.service.reject(this.projectId, this.measurementId, reason).subscribe(() => this.reloadDetail());
    });
  }

  private reloadDetail() {
    this.service.detail(this.projectId, this.measurementId).subscribe(value => this.detail.set(value));
    this.service.history(this.projectId, this.measurementId).subscribe(value => this.history.set(value));
  }

  private openMemoDialog(targetItemId: string, existingLines: { description: string; formula: string; value: number }[]) {
    const seed = existingLines.length > 0 ? existingLines[existingLines.length - 1] : null;
    const ref = this.dialog.open(MeasurementMemoDialogComponent, { width: '520px', data: seed });
    ref.afterClosed().subscribe(result => {
      if (!result) return;
      const lines = [...existingLines, result];
      const total = lines.reduce((sum, line) => sum + (line.value || 0), 0);
      this.service.saveMemo(this.projectId, this.measurementId, targetItemId, lines, total).subscribe(() => {
        this.lastMemo.set(result);
      });
    });
  }
}
