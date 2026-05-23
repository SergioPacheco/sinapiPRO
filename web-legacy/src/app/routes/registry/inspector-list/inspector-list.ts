import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-inspector-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class InspectorListComponent {
  title = 'Fiscais';
  subtitle = 'Cadastro de fiscais e inspetores';
  apiPath = '/registry/inspectors';
  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Documento', field: 'document', width: '140px' },
    { header: 'Cargo', field: 'role' },
    { header: 'Organização', field: 'organization' },
    { header: 'Telefone', field: 'phone', width: '130px' },
    { header: 'Email', field: 'email' },
  ];
  fields: FieldConfig[] = [
    { key: 'name', label: 'Nome', required: true },
    { key: 'document', label: 'Documento' },
    { key: 'role', label: 'Cargo' },
    { key: 'organization', label: 'Organização' },
    { key: 'phone', label: 'Telefone' },
    { key: 'email', label: 'Email' },
  ];
}
