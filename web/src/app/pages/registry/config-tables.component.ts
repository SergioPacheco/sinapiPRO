import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { TabViewModule } from 'primeng/tabview';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TreeTableModule } from 'primeng/treetable';

@Component({
  selector: 'app-config-tables',
  standalone: true,
  imports: [FormsModule, DecimalPipe, TabViewModule, TableModule, ButtonModule, DialogModule, InputTextModule, TreeTableModule],
  template: `
    <h3 style="margin:0 0 1rem;color:var(--sp-text)">Tabelas do Sistema</h3>

    <p-tabView>
      <p-tabPanel header="Plano de Contas">
        <p-button label="Nova Conta" icon="pi pi-plus" size="small" class="mb-2" (onClick)="showCoaDialog = true; coaEditing = {}" />
        <p-table [value]="chartOfAccounts()" styleClass="p-datatable-sm" [paginator]="true" [rows]="20">
          <ng-template pTemplate="header"><tr><th>Código</th><th>Nome</th><th>Tipo</th><th>Nível</th><th></th></tr></ng-template>
          <ng-template pTemplate="body" let-c><tr>
            <td style="font-family:monospace">{{c.code}}</td><td>{{c.name}}</td><td>{{c.type}}</td><td>{{c.level}}</td>
            <td><p-button icon="pi pi-pencil" [text]="true" size="small" (onClick)="coaEditing=c;showCoaDialog=true" /></td>
          </tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Categorias de Insumos">
        <p-button label="Nova Categoria" icon="pi pi-plus" size="small" class="mb-2" (onClick)="showCatDialog = true; catEditing = {}" />
        <p-table [value]="inputCategories()" styleClass="p-datatable-sm" [paginator]="true" [rows]="20">
          <ng-template pTemplate="header"><tr><th>Código</th><th>Nome</th><th>Nível</th><th></th></tr></ng-template>
          <ng-template pTemplate="body" let-c><tr>
            <td style="font-family:monospace">{{c.code}}</td><td>{{c.name}}</td><td>{{c.level}}</td>
            <td><p-button icon="pi pi-pencil" [text]="true" size="small" (onClick)="catEditing=c;showCatDialog=true" /></td>
          </tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Condições de Pagamento">
        <p-table [value]="paymentConditions()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Nome</th><th>Parcelas</th><th>Intervalo (dias)</th></tr></ng-template>
          <ng-template pTemplate="body" let-c><tr><td>{{c.name}}</td><td>{{c.installments}}</td><td>{{c.intervalDays}}</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Centros de Custo">
        <p-table [value]="costCenters()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Código</th><th>Nome</th><th>Ativo</th></tr></ng-template>
          <ng-template pTemplate="body" let-c><tr><td style="font-family:monospace">{{c.code}}</td><td>{{c.name}}</td><td>{{c.active ? 'Sim' : 'Não'}}</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Índices Econômicos">
        <p-table [value]="indices()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Código</th><th>Nome</th><th>Fonte</th></tr></ng-template>
          <ng-template pTemplate="body" let-i><tr><td>{{i.code}}</td><td>{{i.name}}</td><td>{{i.source}}</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="BDI">
        <p-table [value]="bdiConfigs()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Nome</th><th>Adm%</th><th>Lucro%</th><th>Financeiro%</th><th>Risco%</th><th>Impostos%</th><th>Total BDI%</th><th>Padrão</th></tr></ng-template>
          <ng-template pTemplate="body" let-b><tr>
            <td>{{b.name}}</td><td>{{b.administration}}</td><td>{{b.profit}}</td><td>{{b.financialCost}}</td>
            <td>{{b.risk}}</td><td>{{b.taxes}}</td><td><strong>{{b.totalBdi}}</strong></td><td>{{b.isDefault ? '✓' : ''}}</td>
          </tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Encargos Sociais">
        <p-table [value]="socialCharges()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Nome</th><th>INSS%</th><th>FGTS%</th><th>13º%</th><th>Férias%</th><th>Aviso%</th><th>Total%</th><th>Padrão</th></tr></ng-template>
          <ng-template pTemplate="body" let-s><tr>
            <td>{{s.name}}</td><td>{{s.inss}}</td><td>{{s.fgts}}</td><td>{{s.thirteenth}}</td>
            <td>{{s.vacation}}</td><td>{{s.notice}}</td><td><strong>{{s.totalPct}}</strong></td><td>{{s.isDefault ? '✓' : ''}}</td>
          </tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Unidades de Medida">
        <p-table [value]="units()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Símbolo</th><th>Descrição</th></tr></ng-template>
          <ng-template pTemplate="body" let-u><tr><td style="font-family:monospace">{{u.symbol}}</td><td>{{u.description}}</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Tipos de Hora">
        <p-table [value]="hourTypes()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Nome</th><th>Multiplicador</th></tr></ng-template>
          <ng-template pTemplate="body" let-h><tr><td>{{h.name}}</td><td>{{h.multiplier}}x</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Contas Bancárias">
        <p-table [value]="bankAccounts()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Banco</th><th>Agência</th><th>Conta</th><th>Tipo</th><th>Titular</th></tr></ng-template>
          <ng-template pTemplate="body" let-b><tr><td>{{b.bankCode}} - {{b.bankName}}</td><td>{{b.agency}}</td><td>{{b.accountNumber}}</td><td>{{b.type}}</td><td>{{b.holder}}</td></tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Alçadas de Aprovação">
        <p-table [value]="authorityLevels()" styleClass="p-datatable-sm">
          <ng-template pTemplate="header"><tr><th>Nome</th><th>Valor Máximo</th><th>Perfil Aprovador</th></tr></ng-template>
          <ng-template pTemplate="body" let-a><tr><td>{{a.name}}</td><td>R$ {{a.maxAmount | number:'1.2-2'}}</td><td>{{a.approverRole}}</td></tr></ng-template>
        </p-table>
      </p-tabPanel>
    </p-tabView>

    <!-- Dialog Plano de Contas -->
    <p-dialog header="Conta Contábil" [(visible)]="showCoaDialog" [modal]="true" [style]="{width:'400px'}">
      <div class="flex flex-column gap-3 pt-2">
        <input pInputText [(ngModel)]="coaEditing.code" placeholder="Código *" />
        <input pInputText [(ngModel)]="coaEditing.name" placeholder="Nome *" />
        <input pInputText [(ngModel)]="coaEditing.type" placeholder="Tipo (RECEITA/DESPESA/ATIVO/PASSIVO)" />
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Salvar" icon="pi pi-check" (onClick)="saveCoa()" />
      </ng-template>
    </p-dialog>

    <!-- Dialog Categoria Insumo -->
    <p-dialog header="Categoria de Insumo" [(visible)]="showCatDialog" [modal]="true" [style]="{width:'400px'}">
      <div class="flex flex-column gap-3 pt-2">
        <input pInputText [(ngModel)]="catEditing.code" placeholder="Código *" />
        <input pInputText [(ngModel)]="catEditing.name" placeholder="Nome *" />
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Salvar" icon="pi pi-check" (onClick)="saveCat()" />
      </ng-template>
    </p-dialog>
  `,
})
export class ConfigTablesComponent {
  private http = inject(HttpClient);
  chartOfAccounts = signal<any[]>([]);
  inputCategories = signal<any[]>([]);
  paymentConditions = signal<any[]>([]);
  costCenters = signal<any[]>([]);
  indices = signal<any[]>([]);
  bdiConfigs = signal<any[]>([]);
  socialCharges = signal<any[]>([]);
  units = signal<any[]>([]);
  hourTypes = signal<any[]>([]);
  bankAccounts = signal<any[]>([]);
  authorityLevels = signal<any[]>([]);

  showCoaDialog = false; coaEditing: any = {};
  showCatDialog = false; catEditing: any = {};

  ngOnInit() {
    this.http.get<any[]>('/registry/chart-of-accounts').subscribe(r => this.chartOfAccounts.set(r || []));
    this.http.get<any[]>('/registry/input-categories').subscribe(r => this.inputCategories.set(r || []));
    this.http.get<any[]>('/registry/payment-conditions').subscribe(r => this.paymentConditions.set(r || []));
    this.http.get<any[]>('/registry/cost-centers').subscribe(r => this.costCenters.set(r || []));
    this.http.get<any[]>('/registry/bdi-configs').subscribe(r => this.bdiConfigs.set(r || []));
    this.http.get<any[]>('/registry/social-charges').subscribe(r => this.socialCharges.set(r || []));
    this.http.get<any[]>('/registry/units-of-measure').subscribe(r => this.units.set(r || []));
    this.http.get<any[]>('/registry/hour-types').subscribe(r => this.hourTypes.set(r || []));
    this.http.get<any[]>('/registry/bank-accounts').subscribe(r => this.bankAccounts.set(r || []));
    this.http.get<any[]>('/registry/authority-levels').subscribe(r => this.authorityLevels.set(r || []));
  }

  saveCoa() {
    const req = this.coaEditing.id
      ? this.http.put(`/registry/chart-of-accounts/${this.coaEditing.id}`, this.coaEditing)
      : this.http.post('/registry/chart-of-accounts', { ...this.coaEditing, level: 1, acceptsEntries: true });
    req.subscribe(() => { this.showCoaDialog = false; this.ngOnInit(); });
  }

  saveCat() {
    const req = this.catEditing.id
      ? this.http.put(`/registry/input-categories/${this.catEditing.id}`, this.catEditing)
      : this.http.post('/registry/input-categories', { ...this.catEditing, level: 1 });
    req.subscribe(() => { this.showCatDialog = false; this.ngOnInit(); });
  }
}
