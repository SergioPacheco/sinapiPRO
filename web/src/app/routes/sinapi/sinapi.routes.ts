import { Routes } from '@angular/router';
import { CompositionListComponent } from './composition-list/composition-list';
import { MaterialListComponent } from './material-list/material-list';

export const routes: Routes = [
  { path: '', redirectTo: 'compositions', pathMatch: 'full' },
  { path: 'compositions', component: CompositionListComponent },
  { path: 'materials', component: MaterialListComponent },
];
