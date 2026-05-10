import { Component, inject, OnInit, signal } from '@angular/core';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { RegistryService } from '../services/registry.service';
import { BankAccount, PaymentMethod } from '../models/registry.model';

@Component({
  selector: 'app-bank-account-list',
  template: `
    <page-header title="Contas Bancárias" subtitle="Bancos e formas de pagamento" />
    <h3 class="text-lg font-semibold mb-2">Contas Bancárias</h3>
    <mtx-grid [columns]="bankColumns" [data]="banks()" [loading]="isLoading()" [rowStriped]="true"
      [pageOnFront]="true" [showPaginator]="false" />
    <h3 class="text-lg font-semibold mt-6 mb-2">Formas de Pagamento</h3>
    <mtx-grid [columns]="pmColumns" [data]="paymentMethods()" [loading]="isLoading()" [rowStriped]="true"
      [pageOnFront]="true" [showPaginator]="false" />
  `,
  imports: [MtxGridModule, PageHeader],
})
export class BankAccountListComponent implements OnInit {
  private readonly service = inject(RegistryService);
  banks = signal<BankAccount[]>([]);
  paymentMethods = signal<PaymentMethod[]>([]);
  isLoading = signal(true);

  bankColumns: MtxGridColumn[] = [
    { header: 'Banco', field: 'bankName' },
    { header: 'Código', field: 'bankCode', width: '80px' },
    { header: 'Agência', field: 'agency', width: '100px' },
    { header: 'Conta', field: 'accountNumber', width: '140px' },
    { header: 'Tipo', field: 'accountType', width: '120px' },
    { header: 'Titular', field: 'holderName' },
  ];

  pmColumns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name' },
    { header: 'Parcelas', field: 'installments', width: '100px' },
  ];

  ngOnInit() {
    this.service.listBankAccounts().subscribe(r => { this.banks.set(r); this.isLoading.set(false); });
    this.service.listPaymentMethods().subscribe(r => this.paymentMethods.set(r));
  }
}
