import { Routes } from '@angular/router';
import { MeasurementListComponent } from './measurement-list/measurement-list';
import { MeasurementFormComponent } from './measurement-form/measurement-form';

export const routes: Routes = [
  { path: '', component: MeasurementListComponent },
  { path: 'new', component: MeasurementFormComponent },
];
