import { Component } from '@angular/core';
import { MtxGridColumn } from '@ng-matero/extensions/grid';
import { SimpleCrudPageComponent } from '../simple-crud-page/simple-crud-page';
import { FieldConfig } from '../simple-crud-page/simple-crud-dialog';

@Component({
  selector: 'app-payment-condition-list',
  standalone: true,
  imports: [SimpleCrudPageComponent],
  template: `<app-simple-crud-page [title]="title" [subtitle]="subtitle" [columns]="columns" [fields]="fields" [apiPath]="apiPath" />`,
})
export class PaymentConditionListComponent {
  title = 'Condições de Pagamento';
  subtitle = 'Prazos e parcelamentos';
  apiPath = '/registry/payment-conditions';
  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Parcelas', field: 'installments', width: '100px' },
    { header: 'Descrição', field: 'description' },
  ];
  fields: FieldConfig[] = [
    { key: 'name', label: 'Nome', required: true },
    { key: 'installments', label: 'Parcelas', type: 'number' },
    { key: 'description', label: 'Descrição' },
  ];
}
