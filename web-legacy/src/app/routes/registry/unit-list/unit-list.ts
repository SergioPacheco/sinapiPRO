import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-unit-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class UnitListComponent {
  title = 'Unidades de Medida';
  subtitle = 'm, m², kg, un, vb, etc.';
  apiPath = '/registry/units';
  columns: MtxGridColumn[] = [
    { header: 'Símbolo', field: 'symbol', width: '100px', sortable: true },
    { header: 'Descrição', field: 'description' },
  ];
  fields: FieldConfig[] = [
    { key: 'symbol', label: 'Símbolo', required: true },
    { key: 'description', label: 'Descrição', required: true },
  ];
}
