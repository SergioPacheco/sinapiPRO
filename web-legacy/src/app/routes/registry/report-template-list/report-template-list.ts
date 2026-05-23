import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-report-template-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class ReportTemplateListComponent {
  title = 'Modelos de Relatório';
  subtitle = 'Templates para geração de relatórios';
  apiPath = '/registry/report-templates';
  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Tipo', field: 'type', width: '140px' },
    { header: 'Descrição', field: 'description' },
  ];
  fields: FieldConfig[] = [
    { key: 'name', label: 'Nome', required: true },
    { key: 'type', label: 'Tipo', type: 'select', required: true, options: [
      { value: 'BUDGET', label: 'Orçamento' },
      { value: 'MEASUREMENT', label: 'Medição' },
      { value: 'DAILY_LOG', label: 'Diário de Obra' },
      { value: 'CONTRACT', label: 'Contrato' },
    ]},
    { key: 'description', label: 'Descrição' },
  ];
}
