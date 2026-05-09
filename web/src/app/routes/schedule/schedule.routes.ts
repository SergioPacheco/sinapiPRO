import { Routes } from '@angular/router';
import { ScheduleListComponent } from './schedule-list/schedule-list';
import { ScheduleFormComponent } from './schedule-form/schedule-form';

export const routes: Routes = [
  { path: '', component: ScheduleListComponent },
  { path: 'new', component: ScheduleFormComponent },
];
