import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { MtxDialog } from '@ng-matero/extensions/dialog';
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
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class BankAccountListComponent implements OnInit {
  private readonly service = inject(RegistryService);
  private readonly dialog = inject(MtxDialog);
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
    {
      header: 'Ações', field: 'actions', width: '80px', pinned: 'right', type: 'button',
      buttons: [
        { type: 'icon', icon: 'delete', tooltip: 'Excluir', color: 'warn', click: (row: BankAccount) => this.confirmDeleteBank(row) },
      ],
    },
  ];

  pmColumns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name' },
    { header: 'Parcelas', field: 'installments', width: '100px' },
    {
      header: 'Ações', field: 'actions', width: '80px', pinned: 'right', type: 'button',
      buttons: [
        { type: 'icon', icon: 'delete', tooltip: 'Excluir', color: 'warn', click: (row: PaymentMethod) => this.confirmDeletePM(row) },
      ],
    },
  ];

  ngOnInit() {
    this.service.listBankAccounts().subscribe(r => { this.banks.set(r); this.isLoading.set(false); });
    this.service.listPaymentMethods().subscribe(r => this.paymentMethods.set(r));
  }

  confirmDeleteBank(bank: BankAccount) {
    this.dialog.confirm('Confirmar exclusão', `Excluir conta "${bank.bankName} - ${bank.accountNumber}"?`, () =>
      this.service.deleteBankAccount(bank.id).subscribe(() =>
        this.service.listBankAccounts().subscribe(r => this.banks.set(r))
      )
    );
  }

  confirmDeletePM(pm: PaymentMethod) {
    this.dialog.confirm('Confirmar exclusão', `Excluir "${pm.name}"?`, () =>
      this.service.deletePaymentMethod(pm.id).subscribe(() =>
        this.service.listPaymentMethods().subscribe(r => this.paymentMethods.set(r))
      )
    );
  }
}
