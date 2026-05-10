import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';
import { ProjectService, Project } from '../services/project.service';

@Component({
  selector: 'app-project-summary',
  template: `
    @if (project()) {
      <!-- Info Cards -->
      <div class="info-row">
        <mat-card class="info-card">
          <mat-icon class="info-icon" color="primary">person</mat-icon>
          <div><strong>Cliente</strong><p class="secondary-text">{{ project()!.customerName }}</p></div>
        </mat-card>
        <mat-card class="info-card">
          <mat-icon class="info-icon" color="primary">location_on</mat-icon>
          <div><strong>Local</strong><p class="secondary-text">{{ project()!.city || '—' }}/{{ project()!.state || '—' }}</p></div>
        </mat-card>
        <mat-card class="info-card">
          <mat-icon class="info-icon" color="primary">calendar_today</mat-icon>
          <div><strong>Prazo</strong><p class="secondary-text">{{ project()!.startDate || '—' }} → {{ project()!.expectedEndDate || '—' }}</p></div>
        </mat-card>
        <mat-card class="info-card">
          <mat-icon class="info-icon" color="primary">payments</mat-icon>
          <div><strong>Valor</strong><p class="secondary-text">{{ project()!.totalBudget ? formatCurrency(project()!.totalBudget!) : '—' }}</p></div>
        </mat-card>
      </div>

      <!-- Quick Access - compact toolbar -->
      <h3 class="section-title">Acesso Rápido</h3>
      <div class="quick-access">
        @for (mod of modules; track mod.path) {
          <a class="quick-link" [routerLink]="mod.path" [title]="mod.label">
            <mat-icon>{{ mod.icon }}</mat-icon>
            <span>{{ mod.label }}</span>
          </a>
        }
      </div>

      <!-- Actions -->
      <div class="actions-row">
        <button mat-flat-button color="primary" (click)="edit()"><mat-icon>edit</mat-icon> Editar Obra</button>
      </div>
    }
  `,
  styles: `
    .info-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 12px; margin-bottom: 24px; }
    .info-card { display: flex; align-items: center; gap: 12px; padding: 16px; }
    .info-card p { margin: 2px 0 0; font-size: 14px; }
    .secondary-text { color: var(--mat-sys-on-surface-variant); }
    .info-icon { font-size: 24px; width: 24px; height: 24px; }
    .section-title { font-size: 16px; font-weight: 500; margin: 0 0 12px; color: var(--mat-sys-on-surface); }
    .quick-access { display: flex; flex-wrap: wrap; gap: 4px; margin-bottom: 24px; padding: 8px; border-radius: 8px; background: var(--mat-sys-surface-container); }
    .quick-link { display: inline-flex; align-items: center; gap: 4px; padding: 6px 12px; border-radius: 6px; text-decoration: none; color: var(--mat-sys-on-surface); font-size: 12px; font-weight: 500; transition: background 0.15s; }
    .quick-link:hover { background: var(--mat-sys-surface-container-highest); }
    .quick-link mat-icon { font-size: 18px; width: 18px; height: 18px; }
    .actions-row { margin-top: 16px; }
  `,
  imports: [MatButtonModule, MatCardModule, MatIconModule, MatProgressBarModule, MatDividerModule, RouterLink],
})
export class ProjectSummaryComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly service = inject(ProjectService);

  project = signal<Project | null>(null);

  modules = [
    { path: '../budgets', label: 'Orçamentos', icon: 'request_quote' },
    { path: '../contracts', label: 'Contratos', icon: 'description' },
    { path: '../schedule', label: 'Cronograma', icon: 'event_note' },
    { path: '../measurements', label: 'Medições', icon: 'straighten' },
    { path: '../daily-logs', label: 'Diário de Obra', icon: 'edit_note' },
    { path: '../procurement', label: 'Suprimentos', icon: 'shopping_cart' },
    { path: '../finance', label: 'Financeiro', icon: 'account_balance' },
    { path: '../job-costing', label: 'Custeio (EVM)', icon: 'trending_up' },
    { path: '../documents', label: 'Documentos', icon: 'folder' },
    { path: '../safety', label: 'Segurança', icon: 'health_and_safety' },
    { path: '../rfi', label: 'RFI', icon: 'help_outline' },
    { path: '../punch-list', label: 'Punch List', icon: 'checklist' },
    { path: '../time-tracking', label: 'Apontamento', icon: 'schedule' },
    { path: '../submittals', label: 'Submittals', icon: 'assignment_turned_in' },
    { path: '../delivery', label: 'Entrega', icon: 'verified' },
  ];

  ngOnInit() {
    const id = this.route.parent!.snapshot.paramMap.get('projectId')!;
    this.service.getById(id).subscribe(p => this.project.set(p));
  }

  edit() { this.router.navigate(['/projects', this.project()!.id, 'edit']); }

  formatCurrency(value: number): string {
    return 'R$ ' + value.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }
}
