import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { TabViewModule } from 'primeng/tabview';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-equipment-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, TabViewModule, TagModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0">Equipamentos</h3>
      <p-button label="Novo Equipamento" icon="pi pi-plus" size="small" (onClick)="showCreate = true" />
    </div>
    <p-table [value]="items()" [loading]="loading()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10" [expandedRowKeys]="expandedRows" dataKey="id">
      <ng-template pTemplate="header"><tr><th style="width:40px"></th><th>Código</th><th>Nome</th><th>Tipo</th><th style="width:120px">Status</th><th style="width:120px">R\$/hora</th></tr></ng-template>
      <ng-template pTemplate="body" let-r let-expanded="expanded">
        <tr>
          <td><p-button [icon]="expanded ? 'pi pi-chevron-down' : 'pi pi-chevron-right'" [text]="true" [pRowToggler]="r" /></td>
          <td class="font-mono">{{ r.code }}</td><td>{{ r.name }}</td><td>{{ r.type }}</td>
          <td><p-tag [value]="r.status" [severity]="statusSev(r.status)" /></td>
          <td>{{ r.hourlyRate | number:'1.2-2' }}</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="rowexpansion" let-r>
        <tr><td colspan="6" class="p-3">
          <p-tabView>
              <p-tabPanel header="Uso"><p class="text-muted">Registros de uso do equipamento {{ r.name }}</p></p-tabPanel>
              <p-tabPanel header="Abastecimento"><p class="text-muted">Registros de abastecimento</p></p-tabPanel>
              <p-tabPanel header="Manutenção"><p class="text-muted">Registros de manutenção</p></p-tabPanel>
          </p-tabView>
        </td></tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center text-muted p-3">Nenhum equipamento</td></tr></ng-template>
    </p-table>

    <p-dialog header="Novo Equipamento" [(visible)]="showCreate" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Código</label><input pInputText [(ngModel)]="form.code" class="w-full" /></div>
        <div><label>Nome</label><input pInputText [(ngModel)]="form.name" class="w-full" /></div>
        <div><label>Tipo</label><input pInputText [(ngModel)]="form.type" class="w-full" /></div>
        <div><label>Valor/hora (R$)</label><p-inputNumber [(ngModel)]="form.hourlyRate" mode="currency" currency="BRL" styleClass="w-full" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Criar" icon="pi pi-check" (onClick)="create()" /></ng-template>
    </p-dialog>
  `,
})
export class EquipmentListComponent implements OnInit {
  private http = inject(HttpClient);
  items = signal<any[]>([]);
  loading = signal(true);
  showCreate = false;
  expandedRows: any = {};
  form: any = { code: '', name: '', type: '', hourlyRate: 0 };

  ngOnInit() { this.load(); }

  load() {
    this.http.get<any>('/equipment').subscribe({
      next: res => { this.items.set(res.content || res); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  create() {
    this.http.post('/equipment', this.form).subscribe(() => {
      this.showCreate = false; this.form = { code: '', name: '', type: '', hourlyRate: 0 }; this.load();
    });
  }

  statusSev(s: string) { return ({ AVAILABLE: 'success', IN_USE: 'info', MAINTENANCE: 'warn' } as any)[s] || 'secondary'; }
}
