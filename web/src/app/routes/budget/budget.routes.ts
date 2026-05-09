import { Routes } from '@angular/router';
import { BudgetListComponent } from './budget-list/budget-list';
import { BudgetFormComponent } from './budget-form/budget-form';

export const routes: Routes = [
  { path: '', component: BudgetListComponent },
  { path: 'new', component: BudgetFormComponent },
  { path: ':id/edit', component: BudgetFormComponent },
];
