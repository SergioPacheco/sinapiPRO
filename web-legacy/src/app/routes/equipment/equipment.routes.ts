import { Routes } from '@angular/router';
import { EquipmentListComponent } from './equipment-list/equipment-list';
import { EquipmentFormComponent } from './equipment-form/equipment-form';

export const routes: Routes = [
  { path: '', component: EquipmentListComponent },
  { path: 'new', component: EquipmentFormComponent },
  { path: ':id/edit', component: EquipmentFormComponent },
];
