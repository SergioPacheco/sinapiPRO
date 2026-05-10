import { Routes } from '@angular/router';
import { RegistryHubComponent } from './registry-hub/registry-hub';
import { ClientListComponent } from './client-list/client-list';
import { EmployeeListComponent } from './employee-list/employee-list';
import { BankAccountListComponent } from './bank-account-list/bank-account-list';
import { TeamListComponent } from './team-list/team-list';

export const routes: Routes = [
  { path: '', component: RegistryHubComponent },
  { path: 'clients', component: ClientListComponent },
  { path: 'employees', component: EmployeeListComponent },
  { path: 'bank-accounts', component: BankAccountListComponent },
  { path: 'teams', component: TeamListComponent },
];
