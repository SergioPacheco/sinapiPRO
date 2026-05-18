import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-cost-center-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class CostCenterListComponent {
  title = 'Centros de Custo';
  subtitle = 'Agrupamento contábil de despesas';
  apiPath = '/registry/cost-centers';
  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'code', width: '120px', sortable: true },
    { header: 'Nome', field: 'name' },
    { header: 'Descrição', field: 'description' },
  ];
  fields: FieldConfig[] = [
    { key: 'code', label: 'Código', required: true },
    { key: 'name', label: 'Nome', required: true },
    { key: 'description', label: 'Descrição' },
  ];
}
