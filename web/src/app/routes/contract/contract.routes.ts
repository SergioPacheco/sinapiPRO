import { Routes } from '@angular/router';
import { ContractListComponent } from './contract-list/contract-list';
import { ContractFormComponent } from './contract-form/contract-form';

export const routes: Routes = [
  { path: '', component: ContractListComponent },
  { path: 'new', component: ContractFormComponent },
  { path: ':id/edit', component: ContractFormComponent },
];
