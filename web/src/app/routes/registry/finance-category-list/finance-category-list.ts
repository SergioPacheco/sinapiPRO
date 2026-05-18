import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-finance-category-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class FinanceCategoryListComponent {
  title = 'Categorias Financeiras';
  subtitle = 'Classificação de despesas e receitas';
  apiPath = '/registry/finance-categories';
  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'code', width: '120px', sortable: true },
    { header: 'Nome', field: 'name' },
    { header: 'Tipo', field: 'type', width: '120px' },
  ];
  fields: FieldConfig[] = [
    { key: 'code', label: 'Código', required: true },
    { key: 'name', label: 'Nome', required: true },
    { key: 'type', label: 'Tipo', type: 'select', required: true, options: [
      { value: 'MATERIAL', label: 'Material' },
      { value: 'LABOR', label: 'Mão de Obra' },
      { value: 'EQUIPMENT', label: 'Equipamento' },
      { value: 'SERVICE', label: 'Serviço' },
      { value: 'OTHER', label: 'Outro' },
    ]},
  ];
}
