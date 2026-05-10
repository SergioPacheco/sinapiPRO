import { Routes } from '@angular/router';
import { BudgetListComponent } from './budget-list/budget-list';
import { BudgetFormComponent } from './budget-form/budget-form';
import { BudgetWorksheetComponent } from './budget-worksheet/budget-worksheet';

export const routes: Routes = [
  { path: '', component: BudgetListComponent },
  { path: 'new', component: BudgetFormComponent },
  { path: ':id/edit', component: BudgetFormComponent },
  { path: ':id/worksheet', component: BudgetWorksheetComponent },
];
