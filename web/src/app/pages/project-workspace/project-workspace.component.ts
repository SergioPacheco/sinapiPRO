import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { TabMenuModule } from 'primeng/tabmenu';
import { TagModule } from 'primeng/tag';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-project-workspace',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TabMenuModule, TagModule],
  template: `
    @if (project(); as p) {
      <div class="workspace-header">
        <div class="flex align-items-center gap-3">
          <div>
            <h2 style="margin:0">{{ p.code }} — {{ p.name }}</h2>
            <span class="text-muted">{{ p.customerName }} {{ p.city ? '| ' + p.city + '/' + p.state : '' }}</span>
          </div>
          <p-tag [value]="statusLabel[p.status] || p.status" [severity]="statusSeverity(p.status)" />
        </div>
      </div>
      <nav class="workspace-tabs">
        @for (tab of tabs; track tab.route) {
          <a class="tab-item" [routerLink]="tab.route" routerLinkActive="active">
            <i [class]="'pi pi-' + tab.icon"></i>
            <span>{{ tab.label }}</span>
          </a>
        }
      </nav>
      <div class="workspace-content">
        <router-outlet />
      </div>
    }
  `,
  styles: [`
    .workspace-header { padding: 0 0 1rem; border-bottom: 1px solid var(--sp-border); margin-bottom: 0; }
    .workspace-tabs {
      display: flex; gap: 0; border-bottom: 1px solid var(--sp-border);
      overflow-x: auto; scrollbar-width: thin;
    }
    .tab-item {
      display: flex; align-items: center; gap: 0.4rem;
      padding: 0.75rem 1rem; font-size: 13px; font-weight: 500;
      color: var(--sp-text-muted); text-decoration: none;
      border-bottom: 2px solid transparent; white-space: nowrap;
      transition: all 0.15s;
    }
    .tab-item:hover { color: var(--sp-text); background: var(--sp-surface-hover); }
    .tab-item.active { color: var(--sp-primary); border-bottom-color: var(--sp-primary); }
    .tab-item i { font-size: 0.9rem; }
    .workspace-content { padding: 1.25rem 0; }
  `],
})
export class ProjectWorkspaceComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  project = signal<any>(null);

  statusLabel: Record<string, string> = {
    PLANNING: 'Planejamento', IN_PROGRESS: 'Em Execução', SUSPENDED: 'Suspensa', COMPLETED: 'Concluída',
  };

  tabs = [
    { label: 'Resumo', icon: 'info-circle', route: 'summary' },
    { label: 'Orçamentos', icon: 'calculator', route: 'budgets' },
    { label: 'Contratos', icon: 'file', route: 'contracts' },
    { label: 'Cronograma', icon: 'calendar', route: 'schedule' },
    { label: 'Medições', icon: 'chart-line', route: 'measurements' },
    { label: 'Execução', icon: 'wrench', route: 'daily-logs' },
    { label: 'Suprimentos', icon: 'truck', route: 'procurement' },
    { label: 'Financeiro', icon: 'wallet', route: 'finance' },
    { label: 'RFI', icon: 'question-circle', route: 'rfi' },
    { label: 'Punch List', icon: 'list-check', route: 'punch-list' },
    { label: 'Documentos', icon: 'folder', route: 'documents' },
  ];

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    this.http.get(`/projects/${id}`).subscribe(p => this.project.set(p));
  }

  statusSeverity(s: string) {
    return ({ IN_PROGRESS: 'success', PLANNING: 'info', SUSPENDED: 'warn', COMPLETED: 'secondary' } as any)[s];
  }
}
