import { Routes } from '@angular/router';
import { RegistryHubComponent } from './registry-hub/registry-hub';
import { ClientListComponent } from './client-list/client-list';
import { EmployeeListComponent } from './employee-list/employee-list';
import { EmployeeFormComponent } from './employee-form/employee-form';
import { BankAccountListComponent } from './bank-account-list/bank-account-list';
import { TeamListComponent } from './team-list/team-list';
import { TeamFormComponent } from './team-form/team-form';

export const routes: Routes = [
  { path: '', component: RegistryHubComponent },
  { path: 'clients', component: ClientListComponent },
  { path: 'employees', component: EmployeeListComponent },
  { path: 'employees/new', component: EmployeeFormComponent },
  { path: 'employees/:id/edit', component: EmployeeFormComponent },
  { path: 'bank-accounts', component: BankAccountListComponent },
  { path: 'teams', component: TeamListComponent },
  { path: 'teams/new', component: TeamFormComponent },
  { path: 'teams/:id/edit', component: TeamFormComponent },
];
