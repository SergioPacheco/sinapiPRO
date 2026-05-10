import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

export interface WorkflowAction {
  label: string;
  icon: string;
  action: string;
  color?: string;
  confirm?: string;
}

@Component({
  selector: 'app-workflow-actions',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatTooltipModule],
  template: `
    @for (a of getActions(); track a.action) {
      <button mat-stroked-button [matTooltip]="a.label" (click)="onAction.emit(a.action)"
              [style.color]="a.color || 'inherit'">
        <mat-icon>{{ a.icon }}</mat-icon> {{ a.label }}
      </button>
    }
  `,
  styles: `
    :host { display: inline-flex; gap: 8px; flex-wrap: wrap; }
    button { font-size: 12px; }
    mat-icon { font-size: 16px; width: 16px; height: 16px; margin-right: 4px; }
  `,
})
export class WorkflowActions {
  @Input() status = '';
  @Input() transitions: Record<string, WorkflowAction[]> = {};
  @Output() onAction = new EventEmitter<string>();

  getActions(): WorkflowAction[] {
    return this.transitions[this.status] || [];
  }
}

// Pre-built transitions
export const MEASUREMENT_TRANSITIONS: Record<string, WorkflowAction[]> = {
  DRAFT: [{ label: 'Submeter', icon: 'send', action: 'submit', color: '#f59e0b' }],
  SUBMITTED: [
    { label: 'Aprovar', icon: 'check_circle', action: 'approve', color: '#10b981' },
    { label: 'Rejeitar', icon: 'cancel', action: 'reject', color: '#ef4444' },
  ],
};

export const ORDER_TRANSITIONS: Record<string, WorkflowAction[]> = {
  PENDING: [
    { label: 'Aprovar', icon: 'check_circle', action: 'approve', color: '#10b981' },
    { label: 'Rejeitar', icon: 'cancel', action: 'reject', color: '#ef4444' },
  ],
  APPROVED: [{ label: 'Registrar Recebimento', icon: 'inventory', action: 'receive', color: '#3b82f6' }],
};

export const BUDGET_TRANSITIONS: Record<string, WorkflowAction[]> = {
  DRAFT: [{ label: 'Enviar para Análise', icon: 'visibility', action: 'submit', color: '#f59e0b' }],
  IN_REVIEW: [
    { label: 'Aprovar', icon: 'check_circle', action: 'approve', color: '#10b981' },
    { label: 'Reprovar', icon: 'cancel', action: 'reject', color: '#ef4444' },
  ],
  APPROVED: [{ label: 'Ativar como Vigente', icon: 'star', action: 'activate', color: '#6366f1' }],
};
