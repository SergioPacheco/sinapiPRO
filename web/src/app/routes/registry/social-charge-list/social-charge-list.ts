import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-social-charge-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class SocialChargeListComponent {
  title = 'Encargos Sociais';
  subtitle = 'Percentuais de encargos por tipo';
  apiPath = '/registry/social-charges';
  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Tipo', field: 'type', width: '120px' },
    { header: 'Percentual %', field: 'percentage', width: '120px' },
  ];
  fields: FieldConfig[] = [
    { key: 'name', label: 'Nome', required: true },
    { key: 'type', label: 'Tipo', type: 'select', required: true, options: [
      { value: 'HOURLY', label: 'Horista' },
      { value: 'MONTHLY', label: 'Mensalista' },
      { value: 'SIMPLES', label: 'Simples' },
    ]},
    { key: 'percentage', label: 'Percentual %', type: 'number', required: true },
  ];
}
