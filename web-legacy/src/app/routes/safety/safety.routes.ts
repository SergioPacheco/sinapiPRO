import { Routes } from '@angular/router';
import { SafetyListComponent } from './safety-list/safety-list';
import { SafetyFormComponent } from './safety-form/safety-form';

export const routes: Routes = [
  { path: '', component: SafetyListComponent },
  { path: 'new', component: SafetyFormComponent },
];
