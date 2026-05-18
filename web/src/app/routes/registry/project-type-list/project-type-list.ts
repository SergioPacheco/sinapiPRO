import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-project-type-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class ProjectTypeListComponent {
  title = 'Tipos de Obra';
  subtitle = 'Residencial, Comercial, Industrial, etc.';
  apiPath = '/registry/project-types';
  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Descrição', field: 'description' },
  ];
  fields: FieldConfig[] = [
    { key: 'name', label: 'Nome', required: true },
    { key: 'description', label: 'Descrição' },
  ];
}
