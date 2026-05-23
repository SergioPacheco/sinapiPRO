import { Routes } from '@angular/router';
import { MeasurementListComponent } from './measurement-list/measurement-list';
import { MeasurementFormComponent } from './measurement-form/measurement-form';
import { MeasurementDetailComponent } from './measurement-detail/measurement-detail';

export const routes: Routes = [
  { path: '', component: MeasurementListComponent },
  { path: 'new', component: MeasurementFormComponent },
  { path: ':id', component: MeasurementDetailComponent },
];
