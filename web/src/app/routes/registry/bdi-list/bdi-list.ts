import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-bdi-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class BdiListComponent {
  title = 'BDI';
  subtitle = 'Templates de BDI reutilizáveis';
  apiPath = '/registry/bdi-templates';
  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Administração %', field: 'administration', width: '120px' },
    { header: 'Lucro %', field: 'profit', width: '100px' },
    { header: 'Custo Financeiro %', field: 'financialCost', width: '140px' },
    { header: 'Impostos %', field: 'taxes', width: '110px' },
    { header: 'Total %', field: 'total', width: '100px' },
  ];
  fields: FieldConfig[] = [
    { key: 'name', label: 'Nome', required: true },
    { key: 'administration', label: 'Administração %', type: 'number', required: true },
    { key: 'profit', label: 'Lucro %', type: 'number', required: true },
    { key: 'financialCost', label: 'Custo Financeiro %', type: 'number', required: true },
    { key: 'taxes', label: 'Impostos %', type: 'number', required: true },
    { key: 'total', label: 'Total %', type: 'number', required: true },
  ];
}
