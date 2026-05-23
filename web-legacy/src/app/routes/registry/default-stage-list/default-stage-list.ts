import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-default-stage-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class DefaultStageListComponent {
  title = 'Etapas Padrão';
  subtitle = 'Template de etapas para orçamentos';
  apiPath = '/registry/default-stages';
  columns: MtxGridColumn[] = [
    { header: 'Ordem', field: 'sortOrder', width: '80px', sortable: true },
    { header: 'Nome', field: 'name' },
    { header: 'Descrição', field: 'description' },
  ];
  fields: FieldConfig[] = [
    { key: 'name', label: 'Nome', required: true },
    { key: 'sortOrder', label: 'Ordem', type: 'number' },
    { key: 'description', label: 'Descrição' },
  ];
}
