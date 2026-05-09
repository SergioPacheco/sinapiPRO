import { Routes } from '@angular/router';
import { ProcurementListComponent } from './procurement-list/procurement-list';
import { QuotationListComponent } from './quotation-list/quotation-list';
import { InventoryListComponent } from './inventory-list/inventory-list';

export const routes: Routes = [
  { path: '', component: ProcurementListComponent },
  { path: 'quotations', component: QuotationListComponent },
  { path: 'inventory', component: InventoryListComponent },
];
