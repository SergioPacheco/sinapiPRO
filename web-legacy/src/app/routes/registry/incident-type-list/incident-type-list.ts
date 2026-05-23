import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-incident-type-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class IncidentTypeListComponent {
  title = 'Tipos de Incidente';
  subtitle = 'Classificação de ocorrências de segurança';
  apiPath = '/registry/incident-types';
  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Severidade', field: 'severity', width: '120px' },
    { header: 'Descrição', field: 'description' },
  ];
  fields: FieldConfig[] = [
    { key: 'name', label: 'Nome', required: true },
    { key: 'severity', label: 'Severidade', type: 'select', required: true, options: [
      { value: 'LOW', label: 'Baixa' },
      { value: 'MEDIUM', label: 'Média' },
      { value: 'HIGH', label: 'Alta' },
      { value: 'CRITICAL', label: 'Crítica' },
    ]},
    { key: 'description', label: 'Descrição' },
  ];
}
