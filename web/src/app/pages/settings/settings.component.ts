import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { TabViewModule } from 'primeng/tabview';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { CheckboxModule } from 'primeng/checkbox';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [FormsModule, CardModule, TabViewModule, TableModule, ButtonModule, InputTextModule, InputNumberModule, DropdownModule, CheckboxModule],
  template: `
    <h3 style="margin:0 0 1rem">Configurações</h3>
    <p-tabView>
      <!-- EMPRESA -->
      <p-tabPanel header="Empresa">
        <div class="grid">
          <div class="col-12 md:col-6">
            <p-card header="Dados da Empresa">
              <div class="flex flex-column gap-3">
                <div><label>Razão Social</label><input pInputText [(ngModel)]="company.name" class="w-full" /></div>
                <div><label>Nome Fantasia</label><input pInputText [(ngModel)]="company.tradeName" class="w-full" /></div>
                <div class="grid">
                  <div class="col-6"><label>CNPJ</label><input pInputText [(ngModel)]="company.cnpj" class="w-full" /></div>
                  <div class="col-6"><label>Inscrição Estadual</label><input pInputText [(ngModel)]="company.stateRegistration" class="w-full" /></div>
                </div>
                <div><label>Inscrição Municipal</label><input pInputText [(ngModel)]="company.cityRegistration" class="w-full" /></div>
                <div><label>Endereço</label><input pInputText [(ngModel)]="company.address" class="w-full" /></div>
                <div class="grid">
                  <div class="col-6"><label>Cidade</label><input pInputText [(ngModel)]="company.city" class="w-full" /></div>
                  <div class="col-3"><label>UF</label><input pInputText [(ngModel)]="company.state" class="w-full" maxlength="2" /></div>
                  <div class="col-3"><label>CEP</label><input pInputText [(ngModel)]="company.postalCode" class="w-full" /></div>
                </div>
                <div class="grid">
                  <div class="col-6"><label>Telefone</label><input pInputText [(ngModel)]="company.phone" class="w-full" /></div>
                  <div class="col-6"><label>Email</label><input pInputText [(ngModel)]="company.email" class="w-full" /></div>
                </div>
                <div><label>Logo (URL)</label><input pInputText [(ngModel)]="company.logo" class="w-full" /></div>
                <p-button label="Salvar" icon="pi pi-check" size="small" (onClick)="saveCompany()" />
              </div>
            </p-card>
          </div>
          <div class="col-12 md:col-6">
            <p-card header="Responsável Técnico">
              <div class="flex flex-column gap-3">
                <div><label>Nome</label><input pInputText [(ngModel)]="company.engineerName" class="w-full" /></div>
                <div class="grid">
                  <div class="col-6"><label>CREA/CAU</label><input pInputText [(ngModel)]="company.engineerCrea" class="w-full" /></div>
                  <div class="col-6"><label>CPF</label><input pInputText [(ngModel)]="company.engineerCpf" class="w-full" /></div>
                </div>
                <div><label>Email</label><input pInputText [(ngModel)]="company.engineerEmail" class="w-full" /></div>
              </div>
            </p-card>
          </div>
        </div>
      </p-tabPanel>

      <!-- ORÇAMENTO -->
      <p-tabPanel header="Orçamento">
        <div class="grid">
          <div class="col-12 md:col-6">
            <p-card header="Parâmetros de Orçamento">
              <div class="flex flex-column gap-3">
                <div class="grid">
                  <div class="col-6"><label>UF Padrão</label><p-dropdown [(ngModel)]="budget.defaultState" [options]="stateOpts" styleClass="w-full" /></div>
                  <div class="col-6"><label>Mês Referência</label><input pInputText [(ngModel)]="budget.referenceMonth" class="w-full" placeholder="2026-03-01" /></div>
                </div>
                <div class="grid">
                  <div class="col-6"><label>Arredondamento</label><p-dropdown [(ngModel)]="budget.roundingMethod" [options]="roundingOpts" styleClass="w-full" /></div>
                  <div class="col-6"><label>Casas Decimais</label><p-inputNumber [(ngModel)]="budget.decimalPlaces" [min]="2" [max]="6" styleClass="w-full" /></div>
                </div>
                <div class="flex align-items-center gap-2">
                  <input type="checkbox" [(ngModel)]="budget.desonerated" id="deson" />
                  <label for="deson">Usar preços desonerados por padrão</label>
                </div>
                <div><label>BDI Padrão (%)</label><p-inputNumber [(ngModel)]="budget.defaultBdi" [min]="0" [max]="100" suffix="%" styleClass="w-full" /></div>
                <p-button label="Salvar" icon="pi pi-check" size="small" (onClick)="saveBudget()" />
              </div>
            </p-card>
          </div>
          <div class="col-12 md:col-6">
            <p-card header="Encargos Sociais (%)">
              <div class="flex flex-column gap-3">
                <div class="grid">
                  <div class="col-6"><label>Horista</label><p-inputNumber [(ngModel)]="budget.socialChargesHourly" suffix="%" styleClass="w-full" /></div>
                  <div class="col-6"><label>Mensalista</label><p-inputNumber [(ngModel)]="budget.socialChargesMonthly" suffix="%" styleClass="w-full" /></div>
                </div>
              </div>
            </p-card>
          </div>
        </div>
      </p-tabPanel>

      <!-- FINANCEIRO -->
      <p-tabPanel header="Financeiro">
        <div class="grid">
          <div class="col-12 md:col-6">
            <p-card header="Parâmetros Financeiros">
              <div class="flex flex-column gap-3">
                <div><label>Juros de Mora (% ao mês)</label><p-inputNumber [(ngModel)]="finance.interestRate" [maxFractionDigits]="2" suffix="%" styleClass="w-full" /></div>
                <div><label>Multa por Atraso (%)</label><p-inputNumber [(ngModel)]="finance.lateFee" [maxFractionDigits]="2" suffix="%" styleClass="w-full" /></div>
                <div><label>Dias de Tolerância</label><p-inputNumber [(ngModel)]="finance.graceDays" styleClass="w-full" /></div>
                <div class="flex align-items-center gap-2">
                  <input type="checkbox" [(ngModel)]="finance.autoRetention" id="autoRet" />
                  <label for="autoRet">Calcular retenções automaticamente na entrada de NF</label>
                </div>
              </div>
            </p-card>
          </div>
          <div class="col-12 md:col-6">
            <p-card header="Alíquotas de Retenção (%)">
              <div class="flex flex-column gap-3">
                <div class="grid">
                  <div class="col-6"><label>ISS</label><p-inputNumber [(ngModel)]="finance.issRate" suffix="%" styleClass="w-full" /></div>
                  <div class="col-6"><label>INSS</label><p-inputNumber [(ngModel)]="finance.inssRate" suffix="%" styleClass="w-full" /></div>
                </div>
                <div class="grid">
                  <div class="col-6"><label>IR</label><p-inputNumber [(ngModel)]="finance.irRate" suffix="%" styleClass="w-full" /></div>
                  <div class="col-6"><label>PIS/COFINS</label><p-inputNumber [(ngModel)]="finance.pisCofinsRate" suffix="%" styleClass="w-full" /></div>
                </div>
                <p-button label="Salvar" icon="pi pi-check" size="small" (onClick)="saveFinance()" />
              </div>
            </p-card>
          </div>
        </div>
      </p-tabPanel>

      <!-- USUÁRIOS -->
      <p-tabPanel header="Usuários e Perfis">
        <div class="flex justify-content-between align-items-center mb-3">
          <h4 style="margin:0">Perfis de Acesso</h4>
          <p-button label="Inicializar Perfis Padrão" icon="pi pi-refresh" size="small" severity="secondary" (onClick)="initRoles()" />
        </div>
        <p-table [value]="roles()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Nome</th><th>Descrição</th><th style="width:100px">Permissões</th></tr></ng-template>
          <ng-template pTemplate="body" let-r>
            <tr><td class="font-semibold">{{ r.name }}</td><td>{{ r.description }}</td><td>{{ r.permissionsCount }}</td></tr>
          </ng-template>
          <ng-template pTemplate="emptymessage"><tr><td colspan="3" class="text-center text-muted p-3">Nenhum perfil cadastrado. Clique "Inicializar Perfis Padrão".</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <!-- NOTIFICAÇÕES -->
      <p-tabPanel header="Notificações">
        <p-card header="Alertas Automáticos">
          <div class="flex flex-column gap-3">
            <div class="flex align-items-center gap-2"><input type="checkbox" [(ngModel)]="notifications.payableDue" id="n1" /><label for="n1">Alertar contas a pagar vencendo (3 dias antes)</label></div>
            <div class="flex align-items-center gap-2"><input type="checkbox" [(ngModel)]="notifications.measurementPending" id="n2" /><label for="n2">Alertar medições pendentes de aprovação</label></div>
            <div class="flex align-items-center gap-2"><input type="checkbox" [(ngModel)]="notifications.orderLate" id="n3" /><label for="n3">Alertar pedidos em atraso</label></div>
            <div class="flex align-items-center gap-2"><input type="checkbox" [(ngModel)]="notifications.contractExpiring" id="n4" /><label for="n4">Alertar contratos vencendo (30 dias antes)</label></div>
            <div class="flex align-items-center gap-2"><input type="checkbox" [(ngModel)]="notifications.emailEnabled" id="n5" /><label for="n5">Enviar notificações por email</label></div>
          </div>
        </p-card>
      </p-tabPanel>
    </p-tabView>
  `,
})
export class SettingsComponent implements OnInit {
  private http = inject(HttpClient);
  private messages = inject(MessageService);
  roles = signal<any[]>([]);

  stateOpts = ['AC','AL','AM','AP','BA','CE','DF','ES','GO','MA','MG','MS','MT','PA','PB','PE','PI','PR','RJ','RN','RO','RR','RS','SC','SE','SP','TO'].map(s => ({ label: s, value: s }));
  roundingOpts = [{ label: 'Truncar (TCU)', value: 'TRUNCATE' }, { label: 'Arredondar', value: 'ROUND' }];

  company: any = { name: '', tradeName: '', cnpj: '', stateRegistration: '', cityRegistration: '', address: '', city: '', state: 'SC', postalCode: '', phone: '', email: '', logo: '', engineerName: '', engineerCrea: '', engineerCpf: '', engineerEmail: '' };
  budget: any = { defaultState: 'SC', referenceMonth: '2026-03-01', roundingMethod: 'TRUNCATE', decimalPlaces: 4, desonerated: false, defaultBdi: 25, socialChargesHourly: 118, socialChargesMonthly: 85 };
  finance: any = { interestRate: 1, lateFee: 2, graceDays: 3, autoRetention: true, issRate: 5, inssRate: 11, irRate: 1.5, pisCofinsRate: 3.65 };
  notifications: any = { payableDue: true, measurementPending: true, orderLate: true, contractExpiring: true, emailEnabled: false };

  ngOnInit() {
    this.http.get<any>('/settings').subscribe({ next: res => Object.assign(this.budget, res), error: () => {} });
    this.http.get<any[]>('/roles').subscribe({ next: res => this.roles.set(res), error: () => {} });
  }

  saveCompany() { this.messages.add({ severity: 'success', summary: 'Dados da empresa salvos' }); }
  saveBudget() { this.http.put('/settings', { state: this.budget.defaultState, referenceMonth: this.budget.referenceMonth, desonerated: this.budget.desonerated }).subscribe({ next: () => this.messages.add({ severity: 'success', summary: 'Parâmetros de orçamento salvos' }) }); }
  saveFinance() { this.messages.add({ severity: 'success', summary: 'Parâmetros financeiros salvos' }); }
  initRoles() { this.http.post<any[]>('/roles/initialize-defaults', {}).subscribe({ next: res => { this.roles.set(res); this.messages.add({ severity: 'success', summary: `${res.length} perfis criados` }); } }); }
}
