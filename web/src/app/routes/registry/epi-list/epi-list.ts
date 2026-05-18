import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-epi-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class EpiListComponent {
  title = 'EPIs';
  subtitle = 'Equipamentos de Proteção Individual';
  apiPath = '/registry/epis';
  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Nº CA', field: 'caNumber', width: '120px' },
    { header: 'Validade (meses)', field: 'validityMonths', width: '140px' },
    { header: 'Descrição', field: 'description' },
  ];
  fields: FieldConfig[] = [
    { key: 'name', label: 'Nome', required: true },
    { key: 'caNumber', label: 'Nº CA' },
    { key: 'validityMonths', label: 'Validade (meses)', type: 'number' },
    { key: 'description', label: 'Descrição' },
  ];
}
