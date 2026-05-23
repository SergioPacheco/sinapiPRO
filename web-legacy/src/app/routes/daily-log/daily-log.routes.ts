import { Routes } from '@angular/router';
import { DailyLogListComponent } from './daily-log-list/daily-log-list';
import { DailyLogFormComponent } from './daily-log-form/daily-log-form';
import { DailyLogDetailComponent } from './daily-log-detail/daily-log-detail';

export const routes: Routes = [
  { path: '', component: DailyLogListComponent },
  { path: 'new', component: DailyLogFormComponent },
  { path: ':id', component: DailyLogDetailComponent },
];
