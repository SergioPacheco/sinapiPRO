import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MtxDialog } from '@ng-matero/extensions/dialog';
import { PageHeader } from '@shared';
import { NextActionService } from '@shared';
import { ContractService } from '../services/contract.service';
import { Contract } from '../models/contract.model';

@Component({
  selector: 'app-contract-list',
  template: `
    <page-header title="Contratos" subtitle="Gestão de contratos de obra">
      <button mat-flat-button color="primary" (click)="create()"><mat-icon>add</mat-icon> Novo Contrato</button>
    </page-header>

    <!-- Workflow Stepper -->
    <div class="workflow-stepper">
      @for (col of columns; track col.status; let i = $index) {
        <div class="workflow-step">
          <div class="step-circle" [style.background]="col.color">{{ getByStatus(col.status).length }}</div>
          <span class="step-label">{{ col.label }}</span>
        </div>
        @if (!$last) { <div class="step-arrow"><mat-icon>arrow_forward</mat-icon></div> }
      }
    </div>

    <!-- Kanban -->
    <div class="kanban-board">
      @for (col of columns; track col.status) {
        <div class="kanban-column">
          <div class="kanban-header" [style.border-top-color]="col.color">
            <span class="kanban-title">{{ col.label }}</span>
          </div>
          <div class="kanban-cards">
            @for (c of getByStatus(col.status); track c.id) {
              <mat-card class="kanban-card">
                <div class="card-header">
                  <strong>{{ c.number }}</strong>
                  <span class="card-amount">{{ formatCurrency(c.originalValue) }}</span>
                </div>
                <p class="card-desc">{{ c.description }}</p>
                <p class="card-dates">{{ c.startDate }} → {{ c.endDate || '—' }}</p>
                <div class="card-actions">
                  @if (c.status === 'DRAFT') {
                    <button mat-stroked-button color="primary" (click)="activate(c)" matTooltip="Ativar contrato">
                      <mat-icon>play_arrow</mat-icon> Ativar
                    </button>
                  }
                  @if (c.status === 'ACTIVE') {
                    <button mat-stroked-button (click)="complete(c)" matTooltip="Concluir contrato">
                      <mat-icon>check</mat-icon> Concluir
                    </button>
                  }
                  <button mat-icon-button (click)="edit(c)" matTooltip="Editar"><mat-icon>edit</mat-icon></button>
                </div>
              </mat-card>
            }
            @if (getByStatus(col.status).length === 0) {
              <div class="kanban-empty">Nenhum contrato</div>
            }
          </div>
        </div>
      }
    </div>
  `,
  styles: `
    .workflow-stepper { display: flex; align-items: center; justify-content: center; gap: 8px; padding: 16px; margin-bottom: 16px; background: var(--mat-sys-surface-container); border-radius: 12px; }
    .workflow-step { display: flex; flex-direction: column; align-items: center; gap: 4px; }
    .step-circle { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-weight: 700; font-size: 14px; }
    .step-label { font-size: 11px; font-weight: 500; text-transform: uppercase; color: var(--mat-sys-on-surface-variant); }
    .step-arrow { color: var(--mat-sys-outline); margin-bottom: 18px; }
    .kanban-board { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
    .kanban-column { background: var(--mat-sys-surface-container-low); border-radius: 12px; padding: 12px; }
    .kanban-header { border-top: 3px solid; padding: 8px 4px 12px; }
    .kanban-title { font-weight: 600; font-size: 14px; }
    .kanban-cards { display: flex; flex-direction: column; gap: 8px; }
    .kanban-card { padding: 12px; }
    .card-header { display: flex; justify-content: space-between; align-items: center; }
    .card-amount { font-size: 13px; color: #4caf50; font-weight: 600; }
    .card-desc { font-size: 13px; margin: 4px 0; color: var(--mat-sys-on-surface-variant); }
    .card-dates { font-size: 12px; color: var(--mat-sys-outline); margin: 0; }
    .card-actions { display: flex; gap: 4px; margin-top: 8px; align-items: center; }
    .kanban-empty { text-align: center; padding: 24px; color: var(--mat-sys-on-surface-variant); font-size: 13px; }
  `,
  imports: [MatButtonModule, MatIconModule, MatCardModule, MatTooltipModule, PageHeader],
})
export class ContractListComponent implements OnInit {
  private readonly service = inject(ContractService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly dialog = inject(MtxDialog);
  private readonly nextAction = inject(NextActionService);
  private projectId = '';

  list = signal<Contract[]>([]);

  columns = [
    { status: 'DRAFT', label: 'Rascunho', color: '#2196f3' },
    { status: 'ACTIVE', label: 'Ativo', color: '#4caf50' },
    { status: 'COMPLETED', label: 'Concluído', color: '#9e9e9e' },
  ];

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.loadData();
  }

  loadData() {
    this.service.list(this.projectId, 0, 100).subscribe(res => this.list.set(res.content));
  }

  getByStatus(status: string) { return this.list().filter(c => c.status === status); }

  formatCurrency(v: number) { return `R$ ${(v ?? 0).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`; }

  activate(c: Contract) {
    this.dialog.confirm('Ativar contrato', `Ativar contrato "${c.number}"?`, () =>
      this.service.activate(this.projectId, c.id).subscribe(() => {
        this.loadData();
        this.nextAction.suggest('contract.activated');
      }));
  }

  complete(c: Contract) {
    this.dialog.confirm('Concluir contrato', `Marcar contrato "${c.number}" como concluído?`, () =>
      this.service.complete(this.projectId, c.id).subscribe(() => this.loadData()));
  }

  edit(c: Contract) { this.router.navigate([c.id, 'edit'], { relativeTo: this.route }); }
  create() { this.router.navigate(['new'], { relativeTo: this.route }); }
}
