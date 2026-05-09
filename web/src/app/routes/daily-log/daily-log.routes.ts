import { Routes } from '@angular/router';
import { DailyLogListComponent } from './daily-log-list/daily-log-list';
import { DailyLogFormComponent } from './daily-log-form/daily-log-form';

export const routes: Routes = [
  { path: '', component: DailyLogListComponent },
  { path: 'new', component: DailyLogFormComponent },
];
