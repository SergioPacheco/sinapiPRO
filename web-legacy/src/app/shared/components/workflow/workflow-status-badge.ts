import { Component, Input } from '@angular/core';

export interface WorkflowConfig {
  [status: string]: { label: string; color: string; icon: string };
}

export const MEASUREMENT_WORKFLOW: WorkflowConfig = {
  DRAFT: { label: 'Rascunho', color: '#3b82f6', icon: 'edit' },
  SUBMITTED: { label: 'Enviada', color: '#f59e0b', icon: 'send' },
  APPROVED: { label: 'Aprovada', color: '#10b981', icon: 'check_circle' },
  PAID: { label: 'Paga', color: '#6b7280', icon: 'paid' },
  REJECTED: { label: 'Rejeitada', color: '#ef4444', icon: 'cancel' },
};

export const BUDGET_WORKFLOW: WorkflowConfig = {
  DRAFT: { label: 'Rascunho', color: '#3b82f6', icon: 'edit' },
  IN_REVIEW: { label: 'Em Análise', color: '#f59e0b', icon: 'visibility' },
  APPROVED: { label: 'Aprovado', color: '#10b981', icon: 'check_circle' },
  REJECTED: { label: 'Reprovado', color: '#ef4444', icon: 'cancel' },
  SUPERSEDED: { label: 'Substituído', color: '#6b7280', icon: 'swap_horiz' },
  CANCELLED: { label: 'Cancelado', color: '#6b7280', icon: 'block' },
};

export const ORDER_WORKFLOW: WorkflowConfig = {
  PENDING: { label: 'Pendente', color: '#f59e0b', icon: 'hourglass_empty' },
  APPROVED: { label: 'Aprovado', color: '#10b981', icon: 'check_circle' },
  REJECTED: { label: 'Rejeitado', color: '#ef4444', icon: 'cancel' },
  PARTIAL: { label: 'Parcial', color: '#3b82f6', icon: 'inventory' },
  RECEIVED: { label: 'Recebido', color: '#6b7280', icon: 'done_all' },
};

export const CONTRACT_WORKFLOW: WorkflowConfig = {
  DRAFT: { label: 'Rascunho', color: '#3b82f6', icon: 'edit' },
  ACTIVE: { label: 'Ativo', color: '#10b981', icon: 'check_circle' },
  SUSPENDED: { label: 'Suspenso', color: '#f59e0b', icon: 'pause_circle' },
  COMPLETED: { label: 'Concluído', color: '#6b7280', icon: 'done' },
  CANCELLED: { label: 'Cancelado', color: '#ef4444', icon: 'block' },
};

@Component({
  selector: 'app-workflow-badge',
  standalone: true,
  template: `
    <span class="wf-badge" [style.--wf-color]="config[status]?.color || '#6b7280'">
      {{ config[status]?.label || status }}
    </span>
  `,
  styles: `
    .wf-badge {
      display: inline-flex; align-items: center; gap: 4px;
      font-size: 11px; font-weight: 600; padding: 3px 10px; border-radius: 4px;
      color: var(--wf-color);
      background: color-mix(in srgb, var(--wf-color) 12%, transparent);
      text-transform: uppercase; letter-spacing: 0.3px;
    }
  `,
})
export class WorkflowStatusBadge {
  @Input() status = '';
  @Input() config: WorkflowConfig = {};
}
