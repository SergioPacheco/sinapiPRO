import { Routes } from '@angular/router';
import { DevelopmentListComponent } from './development-list/development-list';
import { UnitListComponent } from './unit-list/unit-list';
import { ProposalListComponent } from './proposal-list/proposal-list';
import { CommercialProposalListComponent } from './commercial-proposal-list/commercial-proposal-list';

export const routes: Routes = [
  { path: '', component: DevelopmentListComponent },
  { path: 'proposals', component: CommercialProposalListComponent },
  { path: ':devId/units', component: UnitListComponent },
  { path: ':devId/proposals', component: ProposalListComponent },
];
