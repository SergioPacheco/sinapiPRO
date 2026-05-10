import { Routes } from '@angular/router';
import { CompositionListComponent } from './composition-list/composition-list';
import { CompositionDetailComponent } from './composition-detail/composition-detail';
import { CompositionFormComponent } from './composition-form/composition-form';
import { MaterialListComponent } from './material-list/material-list';
import { MaterialDetailComponent } from './material-detail/material-detail';
import { MaterialFormComponent } from './material-form/material-form';
import { SinapiImportComponent } from './sinapi-import/sinapi-import';

export const routes: Routes = [
  { path: '', redirectTo: 'compositions', pathMatch: 'full' },
  { path: 'compositions', component: CompositionListComponent },
  { path: 'compositions/new', component: CompositionFormComponent },
  { path: 'compositions/:id', component: CompositionDetailComponent },
  { path: 'compositions/:id/edit', component: CompositionFormComponent },
  { path: 'materials', component: MaterialListComponent },
  { path: 'materials/new', component: MaterialFormComponent },
  { path: 'materials/:id', component: MaterialDetailComponent },
  { path: 'materials/:id/edit', component: MaterialFormComponent },
  { path: 'import', component: SinapiImportComponent },
];
