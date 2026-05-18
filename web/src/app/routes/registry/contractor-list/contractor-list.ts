import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-contractor-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class ContractorListComponent {
  title = 'Empreiteiros';
  subtitle = 'Cadastro de empreiteiros e subcontratados';
  apiPath = '/registry/contractors';
  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'CPF/CNPJ', field: 'document', width: '140px' },
    { header: 'Especialidade', field: 'specialty' },
    { header: 'Telefone', field: 'phone', width: '130px' },
    { header: 'Email', field: 'email' },
    { header: 'Cidade', field: 'city', width: '120px' },
    { header: 'UF', field: 'state', width: '50px' },
  ];
  fields: FieldConfig[] = [
    { key: 'name', label: 'Nome', required: true },
    { key: 'document', label: 'CPF/CNPJ' },
    { key: 'specialty', label: 'Especialidade' },
    { key: 'phone', label: 'Telefone' },
    { key: 'email', label: 'Email' },
    { key: 'city', label: 'Cidade' },
    { key: 'state', label: 'UF' },
  ];
}
