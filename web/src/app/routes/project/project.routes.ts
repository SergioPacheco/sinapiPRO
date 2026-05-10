import { Routes } from '@angular/router';
import { ProjectListComponent } from './project-list/project-list';
import { ProjectFormComponent } from './project-form/project-form';
import { ProjectWorkspaceComponent } from './project-workspace/project-workspace';
import { ProjectSummaryComponent } from './project-summary/project-summary';

export const routes: Routes = [
  { path: '', component: ProjectListComponent },
  { path: 'new', component: ProjectFormComponent },
  { path: ':projectId/edit', component: ProjectFormComponent },
  {
    path: ':projectId',
    component: ProjectWorkspaceComponent,
    children: [
      { path: '', redirectTo: 'summary', pathMatch: 'full' },
      { path: 'summary', component: ProjectSummaryComponent },
      { path: 'budgets', loadChildren: () => import('../budget/budget.routes').then(m => m.routes) },
      { path: 'contracts', loadChildren: () => import('../contract/contract.routes').then(m => m.routes) },
      { path: 'measurements', loadChildren: () => import('../measurement/measurement.routes').then(m => m.routes) },
      { path: 'daily-logs', loadChildren: () => import('../daily-log/daily-log.routes').then(m => m.routes) },
      { path: 'schedule', loadChildren: () => import('../schedule/schedule.routes').then(m => m.routes) },
      { path: 'procurement', loadChildren: () => import('../procurement/procurement.routes').then(m => m.routes) },
      { path: 'job-costing', loadChildren: () => import('../job-costing/job-costing.routes').then(m => m.routes) },
      { path: 'safety', loadChildren: () => import('../safety/safety.routes').then(m => m.routes) },
      { path: 'finance', loadChildren: () => import('../finance/finance.routes').then(m => m.routes) },
      { path: 'documents', loadChildren: () => import('../documents/documents.routes').then(m => m.routes) },
      { path: 'rfi', loadChildren: () => import('../rfi/rfi.routes').then(m => m.routes) },
      { path: 'punch-list', loadChildren: () => import('../punch-list/punch-list.routes').then(m => m.routes) },
      { path: 'time-tracking', loadChildren: () => import('../time-tracking/time-tracking.routes').then(m => m.routes) },
      { path: 'submittals', loadChildren: () => import('../submittals/submittals.routes').then(m => m.routes) },
      { path: 'delivery', loadChildren: () => import('../delivery/delivery.routes').then(m => m.routes) },
    ],
  },
];
