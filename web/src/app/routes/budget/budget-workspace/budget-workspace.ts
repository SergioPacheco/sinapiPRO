import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { BudgetService } from '../services/budget.service';
import { Budget } from '../models/budget.model';

@Component({
  selector: 'app-budget-workspace',
  template: `
    @if (budget) {
      <div class="workspace-header">
        <h2>{{ budget.code }} — {{ budget.title }}</h2>
        <span class="subtitle">{{ budget.customerName }} | {{ budget.status }}</span>
      </div>
      <nav mat-tab-nav-bar [tabPanel]="tabPanel">
        @for (tab of tabs; track tab.path) {
          <a mat-tab-link [routerLink]="tab.path" routerLinkActive #rla="routerLinkActive" [active]="rla.isActive">
            <mat-icon>{{ tab.icon }}</mat-icon>&nbsp;{{ tab.label }}
          </a>
        }
      </nav>
      <mat-tab-nav-panel #tabPanel>
        <router-outlet />
      </mat-tab-nav-panel>
    }
  `,
  styles: `
    .workspace-header { padding: 16px 24px 8px; }
    .workspace-header h2 { margin: 0; }
    .subtitle { color: var(--mat-sys-on-surface-variant); font-size: 14px; }
  `,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatTabsModule, MatIconModule],
})
export class BudgetWorkspaceComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly budgetService = inject(BudgetService);

  budget: Budget | null = null;

  tabs = [
    { path: 'contracts', label: 'Contratos', icon: 'description' },
    { path: 'measurements', label: 'Medições', icon: 'straighten' },
    { path: 'daily-logs', label: 'Diário', icon: 'edit_note' },
    { path: 'schedule', label: 'Cronograma', icon: 'event_note' },
    { path: 'procurement', label: 'Suprimentos', icon: 'shopping_cart' },
    { path: 'job-costing', label: 'Job Costing', icon: 'account_balance' },
  ];

  ngOnInit() {
    const projectId = this.route.snapshot.paramMap.get('projectId')!;
    this.budgetService.getById(projectId).subscribe(b => this.budget = b);
  }
}
