import { Routes } from '@angular/router';
import { PayableListComponent } from './payable-list/payable-list';
import { ReceivableListComponent } from './receivable-list/receivable-list';
import { CashFlowComponent } from './cash-flow/cash-flow';
import { InvoiceListComponent } from './invoice-list/invoice-list';

export const routes: Routes = [
  { path: '', redirectTo: 'cash-flow', pathMatch: 'full' },
  { path: 'cash-flow', component: CashFlowComponent },
  { path: 'payables', component: PayableListComponent },
  { path: 'receivables', component: ReceivableListComponent },
  { path: 'invoices', component: InvoiceListComponent },
];
