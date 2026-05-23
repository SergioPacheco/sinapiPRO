import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { ProjectService, Project, ProjectDashboard } from '../services/project.service';

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
        @for (tab of visibleTabs; track tab.path) {
          <a mat-tab-link [routerLink]="tab.path" routerLinkActive #rla="routerLinkActive" [active]="rla.isActive"
             [matBadge]="getBadge(tab.path)" [matBadgeHidden]="!getBadge(tab.path)" matBadgeColor="warn" matBadgeSize="small">
            <mat-icon>{{ tab.icon }}</mat-icon>&nbsp;{{ tab.label }}
          </a>
        }
        <a mat-tab-link [matMenuTriggerFor]="overflowMenu"
           [matBadge]="hasOverflowBadge() ? '!' : ''" [matBadgeHidden]="!hasOverflowBadge()" matBadgeColor="warn" matBadgeSize="small">
          <mat-icon>more_horiz</mat-icon>&nbsp;Mais
        </a>
      </nav>
      <mat-menu #overflowMenu="matMenu">
        @for (tab of overflowTabs; track tab.path) {
          <a mat-menu-item [routerLink]="tab.path">
            <mat-icon>{{ tab.icon }}</mat-icon>
            <span>{{ tab.label }}</span>
            @if (getBadge(tab.path)) {
              <span class="overflow-badge">{{ getBadge(tab.path) }}</span>
            }
          </a>
        }
      </mat-menu>
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
    .overflow-badge { margin-left: 8px; background: #f44336; color: white; border-radius: 50%; width: 18px; height: 18px; display: inline-flex; align-items: center; justify-content: center; font-size: 11px; }
  `,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatTabsModule, MatIconModule, MatChipsModule, MatBadgeModule, MatMenuModule, MatButtonModule],
})
export class ProjectWorkspaceComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly projectService = inject(ProjectService);

  project: Project | null = null;
  dashboard = signal<ProjectDashboard | null>(null);

  statusLabel: Record<string, string> = {
    PLANNING: 'Planejamento', IN_PROGRESS: 'Em Execução', SUSPENDED: 'Suspensa',
    COMPLETED: 'Concluída', CANCELLED: 'Cancelada',
  };

  private readonly allTabs = [
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

  visibleTabs = this.allTabs.slice(0, 7);
  overflowTabs = this.allTabs.slice(7);

  hasOverflowBadge = computed(() => this.overflowTabs.some(tab => !!this.getBadge(tab.path)));

  ngOnInit() {
    const projectId = this.route.snapshot.paramMap.get('projectId')!;
    this.projectService.getById(projectId).subscribe(p => this.project = p);
    this.projectService.getDashboard(projectId).subscribe(d => this.dashboard.set(d));
  }

  getBadge(tabPath: string): string | null {
    const d = this.dashboard();
    if (!d) return null;
    if (tabPath === 'measurements' && d.execution.pendingMeasurements > 0) return String(d.execution.pendingMeasurements);
    if (tabPath === 'procurement' && d.execution.pendingOrders > 0) return String(d.execution.pendingOrders);
    if (tabPath === 'budgets' && !d.planning.hasBudget) return '!';
    if (tabPath === 'schedule' && d.planning.hasBudget && !d.planning.hasSchedule) return '!';
    if (tabPath === 'contracts' && d.planning.hasBudget && !d.planning.hasContract) return '!';
    return null;
  }
}
