import { Component, input, output, signal, computed, inject, TemplateRef, contentChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DrawerModule } from 'primeng/drawer';
import { ButtonModule } from 'primeng/button';
import { StepsModule } from 'primeng/steps';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { MenuItem } from 'primeng/api';

// === StatusTag ===
@Component({
  selector: 'sp-status',
  standalone: true,
  imports: [TagModule],
  template: `<p-tag [value]="label()" [severity]="severity()" [rounded]="true" />`,
})
export class StatusTagComponent {
  status = input.required<string>();
  private readonly map: Record<string, { label: string; severity: any }> = {
    DRAFT: { label: 'Rascunho', severity: 'secondary' },
    PLANNING: { label: 'Planejamento', severity: 'info' },
    IN_PROGRESS: { label: 'Em Execução', severity: 'success' },
    SUBMITTED: { label: 'Submetida', severity: 'info' },
    APPROVED: { label: 'Aprovada', severity: 'success' },
    REJECTED: { label: 'Rejeitada', severity: 'danger' },
    PAID: { label: 'Paga', severity: 'success' },
    PENDING: { label: 'Pendente', severity: 'warn' },
    SUSPENDED: { label: 'Suspensa', severity: 'warn' },
    COMPLETED: { label: 'Concluída', severity: 'secondary' },
    CANCELLED: { label: 'Cancelada', severity: 'danger' },
    RECEIVED: { label: 'Recebido', severity: 'success' },
    PARTIAL: { label: 'Parcial', severity: 'warn' },
    OVERDUE: { label: 'Vencida', severity: 'danger' },
  };
  label = computed(() => this.map[this.status()]?.label || this.status());
  severity = computed(() => this.map[this.status()]?.severity || 'secondary');
}

// === CurrencyDisplay ===
@Component({
  selector: 'sp-currency',
  standalone: true,
  template: `<span class="currency">{{ formatted() }}</span>`,
  styles: [`:host { font-variant-numeric: tabular-nums; }`],
})
export class CurrencyDisplayComponent {
  value = input.required<number>();
  formatted = computed(() => {
    const v = this.value();
    if (v == null) return '—';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL', minimumFractionDigits: 2 }).format(v);
  });
}

// === EmptyState ===
@Component({
  selector: 'sp-empty',
  standalone: true,
  imports: [ButtonModule],
  template: `
    <div class="empty-state">
      <i [class]="'pi pi-' + icon()" style="font-size:2.5rem;color:var(--sp-text-muted)"></i>
      <h3>{{ title() }}</h3>
      <p class="text-muted">{{ message() }}</p>
      @if (actionLabel()) {
        <p-button [label]="actionLabel()!" [icon]="'pi pi-plus'" (onClick)="action.emit()" />
      }
    </div>
  `,
  styles: [`.empty-state { text-align:center; padding:3rem 1rem; } h3 { margin:1rem 0 0.25rem; } p { margin:0 0 1rem; }`],
})
export class EmptyStateComponent {
  icon = input('inbox');
  title = input.required<string>();
  message = input('');
  actionLabel = input<string>();
  action = output<void>();
}

// === InlineCreateDrawer ===
@Component({
  selector: 'sp-drawer',
  standalone: true,
  imports: [DrawerModule, ButtonModule],
  template: `
    <p-drawer [(visible)]="visible" [header]="header()" position="right" [style]="{width:'420px'}">
      <ng-content />
      <ng-template pTemplate="footer">
        <div class="flex gap-2 justify-content-end">
          <p-button label="Cancelar" severity="secondary" (onClick)="close()" />
          <p-button [label]="saveLabel()" icon="pi pi-check" (onClick)="save.emit()" [loading]="loading()" />
        </div>
      </ng-template>
    </p-drawer>
  `,
})
export class InlineCreateDrawerComponent {
  header = input.required<string>();
  saveLabel = input('Salvar');
  loading = input(false);
  save = output<void>();
  visible = false;

  open() { this.visible = true; }
  close() { this.visible = false; }
}

// === WizardStepper ===
@Component({
  selector: 'sp-wizard',
  standalone: true,
  imports: [StepsModule, ButtonModule],
  template: `
    <p-steps [model]="steps()" [activeIndex]="activeIndex()" [readonly]="true" styleClass="mb-4" />
    <div class="wizard-content">
      <ng-content />
    </div>
    <div class="flex justify-content-between mt-4">
      <p-button label="Voltar" icon="pi pi-arrow-left" severity="secondary" [disabled]="activeIndex() === 0" (onClick)="prev()" />
      @if (activeIndex() < steps().length - 1) {
        <p-button label="Próximo" icon="pi pi-arrow-right" iconPos="right" (onClick)="next()" />
      } @else {
        <p-button [label]="finishLabel()" icon="pi pi-check" (onClick)="finish.emit()" [loading]="finishing()" />
      }
    </div>
  `,
})
export class WizardStepperComponent {
  steps = input.required<MenuItem[]>();
  finishLabel = input('Concluir');
  finishing = input(false);
  finish = output<void>();
  activeIndex = signal(0);

  next() { this.activeIndex.update(i => Math.min(i + 1, this.steps().length - 1)); }
  prev() { this.activeIndex.update(i => Math.max(i - 1, 0)); }
  reset() { this.activeIndex.set(0); }
}

// === ConfirmAction ===
@Component({
  selector: 'sp-confirm',
  standalone: true,
  imports: [DialogModule, ButtonModule],
  template: `
    <p-dialog [header]="header()" [(visible)]="visible" [style]="{width:'380px'}" [modal]="true">
      <p>{{ message() }}</p>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="visible = false" />
        <p-button [label]="confirmLabel()" [severity]="severity()" (onClick)="onConfirm()" />
      </ng-template>
    </p-dialog>
  `,
})
export class ConfirmActionComponent {
  header = input('Confirmar');
  message = input.required<string>();
  confirmLabel = input('Confirmar');
  severity = input<any>('danger');
  confirmed = output<void>();
  visible = false;

  show() { this.visible = true; }
  onConfirm() { this.visible = false; this.confirmed.emit(); }
}
