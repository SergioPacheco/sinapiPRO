import { Routes } from '@angular/router';
import { SettingsListComponent } from './settings-list/settings-list';
import { RoleListComponent } from './role-list/role-list';

export const routes: Routes = [
  { path: '', component: SettingsListComponent },
  { path: 'roles', component: RoleListComponent },
];
