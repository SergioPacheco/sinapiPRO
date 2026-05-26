import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TabViewModule } from 'primeng/tabview';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TreeTableModule } from 'primeng/treetable';

@Component({
  selector: 'app-config-tables',
  standalone: true,
  imports: [FormsModule, TabViewModule, TableModule, ButtonModule, DialogModule, InputTextModule, TreeTableModule],
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

  showCoaDialog = false; coaEditing: any = {};
  showCatDialog = false; catEditing: any = {};

  ngOnInit() {
    this.http.get<any[]>('/registry/chart-of-accounts').subscribe(r => this.chartOfAccounts.set(r || []));
    this.http.get<any[]>('/registry/input-categories').subscribe(r => this.inputCategories.set(r || []));
    this.http.get<any[]>('/registry/payment-conditions').subscribe(r => this.paymentConditions.set(r || []));
    this.http.get<any[]>('/registry/cost-centers').subscribe(r => this.costCenters.set(r || []));
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
