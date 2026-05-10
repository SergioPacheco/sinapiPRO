import { Routes } from '@angular/router';
import { ClientListComponent } from './client-list/client-list';
import { EmployeeListComponent } from './employee-list/employee-list';
import { BankAccountListComponent } from './bank-account-list/bank-account-list';

export const routes: Routes = [
  { path: '', redirectTo: 'clients', pathMatch: 'full' },
  { path: 'clients', component: ClientListComponent },
  { path: 'employees', component: EmployeeListComponent },
  { path: 'bank-accounts', component: BankAccountListComponent },
];
