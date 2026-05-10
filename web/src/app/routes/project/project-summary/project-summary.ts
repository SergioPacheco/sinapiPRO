import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatRippleModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';
import { ProjectService, Project } from '../services/project.service';

@Component({
  selector: 'app-project-summary',
  template: `
    @if (project()) {
      <!-- Info Cards -->
      <div class="info-row">
        <mat-card class="info-card">
          <mat-icon class="info-icon text-blue">person</mat-icon>
          <div><strong>Cliente</strong><p>{{ project()!.customerName }}</p></div>
        </mat-card>
        <mat-card class="info-card">
          <mat-icon class="info-icon text-green">location_on</mat-icon>
          <div><strong>Local</strong><p>{{ project()!.city || '—' }}/{{ project()!.state || '—' }}</p></div>
        </mat-card>
        <mat-card class="info-card">
          <mat-icon class="info-icon text-orange">calendar_today</mat-icon>
          <div><strong>Prazo</strong><p>{{ project()!.startDate || '—' }} → {{ project()!.expectedEndDate || '—' }}</p></div>
        </mat-card>
        <mat-card class="info-card">
          <mat-icon class="info-icon text-purple">payments</mat-icon>
          <div><strong>Valor</strong><p>{{ project()!.totalBudget ? formatCurrency(project()!.totalBudget!) : '—' }}</p></div>
        </mat-card>
      </div>

      <!-- Quick Access Modules -->
      <h3 class="section-title">Acesso Rápido</h3>
      <div class="modules-grid">
        @for (mod of modules; track mod.path) {
          <mat-card class="module-card" matRipple [routerLink]="mod.path">
            <mat-icon [style.color]="mod.color">{{ mod.icon }}</mat-icon>
            <span>{{ mod.label }}</span>
            @if (mod.badge) {
              <span class="badge" [style.background]="mod.color">{{ mod.badge }}</span>
            }
          </mat-card>
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
    .info-card p { margin: 2px 0 0; color: rgba(0,0,0,.6); font-size: 14px; }
    .info-icon { font-size: 32px; width: 32px; height: 32px; }
    .text-blue { color: #1976d2; } .text-green { color: #4caf50; } .text-orange { color: #ff9800; } .text-purple { color: #9c27b0; }
    .section-title { font-size: 16px; font-weight: 500; margin: 0 0 12px; }
    .modules-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 12px; margin-bottom: 24px; }
    .module-card { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 20px 12px; cursor: pointer; text-align: center; position: relative; transition: transform 0.2s, box-shadow 0.2s; text-decoration: none; color: inherit; }
    .module-card:hover { transform: translateY(-2px); box-shadow: 0 4px 16px rgba(0,0,0,.1); }
    .module-card mat-icon { font-size: 36px; width: 36px; height: 36px; }
    .module-card span { font-size: 13px; font-weight: 500; }
    .badge { position: absolute; top: 8px; right: 8px; color: white; font-size: 11px; padding: 2px 6px; border-radius: 10px; }
    .actions-row { margin-top: 16px; }
  `,
  imports: [MatButtonModule, MatCardModule, MatIconModule, MatProgressBarModule, MatRippleModule, MatDividerModule, RouterLink],
})
export class ProjectSummaryComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly service = inject(ProjectService);

  project = signal<Project | null>(null);

  modules = [
    { path: '../budgets', label: 'Orçamentos', icon: 'request_quote', color: '#1976d2', badge: null },
    { path: '../contracts', label: 'Contratos', icon: 'description', color: '#7b1fa2', badge: null },
    { path: '../schedule', label: 'Cronograma', icon: 'event_note', color: '#388e3c', badge: null },
    { path: '../measurements', label: 'Medições', icon: 'straighten', color: '#f57c00', badge: null },
    { path: '../daily-logs', label: 'Diário de Obra', icon: 'edit_note', color: '#0097a7', badge: null },
    { path: '../procurement', label: 'Suprimentos', icon: 'shopping_cart', color: '#5d4037', badge: null },
    { path: '../finance', label: 'Financeiro', icon: 'account_balance', color: '#c62828', badge: null },
    { path: '../job-costing', label: 'Custeio (EVM)', icon: 'trending_up', color: '#1565c0', badge: null },
    { path: '../documents', label: 'Documentos', icon: 'folder', color: '#455a64', badge: null },
    { path: '../safety', label: 'Segurança', icon: 'health_and_safety', color: '#e65100', badge: null },
    { path: '../rfi', label: 'RFI', icon: 'help_outline', color: '#6a1b9a', badge: null },
    { path: '../punch-list', label: 'Punch List', icon: 'checklist', color: '#ad1457', badge: null },
    { path: '../time-tracking', label: 'Apontamento', icon: 'schedule', color: '#00695c', badge: null },
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
