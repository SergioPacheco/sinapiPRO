import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { ProjectService, Project } from '../services/project.service';

@Component({
  selector: 'app-project-workspace',
  template: `
    @if (project) {
      <div class="workspace-header">
        <h2>{{ project.code }} — {{ project.name }}</h2>
        <span class="subtitle">
          {{ project.customerName }}
          @if (project.city) { | {{ project.city }}/{{ project.state }} }
          | <mat-chip-set><mat-chip>{{ statusLabel[project.status] || project.status }}</mat-chip></mat-chip-set>
        </span>
      </div>
      <nav mat-tab-nav-bar [tabPanel]="tabPanel">
        @for (tab of tabs; track tab.path) {
          <a mat-tab-link [routerLink]="tab.path" routerLinkActive #rla="routerLinkActive" [active]="rla.isActive">
            <mat-icon>{{ tab.icon }}</mat-icon>&nbsp;{{ tab.label }}
          </a>
        }
      </nav>
      <mat-tab-nav-panel #tabPanel>
        <div style="padding: 16px 0;">
          <router-outlet />
        </div>
      </mat-tab-nav-panel>
    }
  `,
  styles: `
    .workspace-header { padding: 16px 24px 8px; }
    .workspace-header h2 { margin: 0; color: var(--mat-sys-on-surface); }
    .subtitle { color: var(--mat-sys-on-surface-variant); font-size: 14px; display: flex; align-items: center; gap: 8px; }
  `,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatTabsModule, MatIconModule, MatChipsModule],
})
export class ProjectWorkspaceComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly projectService = inject(ProjectService);

  project: Project | null = null;

  statusLabel: Record<string, string> = {
    PLANNING: 'Planejamento', IN_PROGRESS: 'Em Execução', SUSPENDED: 'Suspensa',
    COMPLETED: 'Concluída', CANCELLED: 'Cancelada',
  };

  tabs = [
    { path: 'summary', label: 'Resumo', icon: 'info' },
    { path: 'budgets', label: 'Orçamentos', icon: 'request_quote' },
    { path: 'contracts', label: 'Contratos', icon: 'description' },
    { path: 'schedule', label: 'Cronograma', icon: 'event_note' },
    { path: 'measurements', label: 'Medições', icon: 'straighten' },
    { path: 'daily-logs', label: 'Execução', icon: 'engineering' },
    { path: 'procurement', label: 'Suprimentos', icon: 'shopping_cart' },
    { path: 'finance', label: 'Financeiro', icon: 'account_balance' },
    { path: 'safety', label: 'Segurança', icon: 'health_and_safety' },
    { path: 'documents', label: 'Documentos', icon: 'folder' },
  ];

  ngOnInit() {
    const projectId = this.route.snapshot.paramMap.get('projectId')!;
    this.projectService.getById(projectId).subscribe(p => this.project = p);
  }
}
