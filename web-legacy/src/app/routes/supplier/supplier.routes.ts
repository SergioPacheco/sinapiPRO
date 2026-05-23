import { Routes } from '@angular/router';
import { SupplierListComponent } from './supplier-list/supplier-list';
import { SupplierFormComponent } from './supplier-form/supplier-form';

export const routes: Routes = [
  { path: '', component: SupplierListComponent },
  { path: 'new', component: SupplierFormComponent },
  { path: ':id/edit', component: SupplierFormComponent },
];
