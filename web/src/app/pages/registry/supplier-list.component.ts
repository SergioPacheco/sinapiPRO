import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { MessageService } from 'primeng/api';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-supplier-list',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, DropdownModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Fornecedores</h2>
      <p-button label="Novo Fornecedor" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <p-table [value]="suppliers()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="15" [globalFilterFields]="['name','document','category']">
      <ng-template pTemplate="caption">
        <input pInputText [(ngModel)]="filterText" placeholder="Buscar fornecedor..." style="width:250px" (input)="filter($event)" />
      </ng-template>
      <ng-template pTemplate="header">
        <tr>
          <th style="width:80px">Código</th>
          <th>Razão Social</th>
          <th style="width:130px">CNPJ/CPF</th>
          <th style="width:90px">Categoria</th>
          <th style="width:120px">Telefone</th>
          <th style="width:80px">Status</th>
          <th style="width:60px"></th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-s>
        <tr>
          <td class="font-mono" style="font-size:0.8rem">{{ s.code }}</td>
          <td>{{ s.name }}</td>
          <td class="font-mono" style="font-size:0.8rem">{{ s.document }}</td>
          <td style="font-size:0.8rem">{{ s.category }}</td>
          <td style="font-size:0.8rem">{{ s.phone }}</td>
          <td><sp-status [status]="s.status || 'APPROVED'" /></td>
          <td><p-button icon="pi pi-pencil" [text]="true" size="small" (onClick)="edit(s)" /></td>
        </tr>
      </ng-template>
    </p-table>

    <!-- Novo/Editar Fornecedor -->
    <p-dialog [header]="form.id ? 'Editar Fornecedor' : 'Novo Fornecedor'" [(visible)]="showNew" [style]="{width:'550px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div class="grid">
          <div class="col-3"><label>Código</label><input pInputText [(ngModel)]="form.code" class="w-full" /></div>
          <div class="col-9"><label>Razão Social</label><input pInputText [(ngModel)]="form.name" class="w-full" /></div>
        </div>
        <div class="grid">
          <div class="col-6"><label>Nome Fantasia</label><input pInputText [(ngModel)]="form.tradeName" class="w-full" /></div>
          <div class="col-6"><label>CNPJ/CPF</label><input pInputText [(ngModel)]="form.document" class="w-full" /></div>
        </div>
        <div class="grid">
          <div class="col-4"><label>Categoria</label><p-dropdown [(ngModel)]="form.category" [options]="categories" styleClass="w-full" /></div>
          <div class="col-4"><label>Telefone</label><input pInputText [(ngModel)]="form.phone" class="w-full" /></div>
          <div class="col-4"><label>Email</label><input pInputText [(ngModel)]="form.email" class="w-full" /></div>
        </div>
        <div class="grid">
          <div class="col-8"><label>Endereço</label><input pInputText [(ngModel)]="form.address" class="w-full" /></div>
          <div class="col-4"><label>Cidade/UF</label><input pInputText [(ngModel)]="form.city" class="w-full" /></div>
        </div>
        <div class="grid">
          <div class="col-4"><label>Contato</label><input pInputText [(ngModel)]="form.contactName" class="w-full" /></div>
          <div class="col-4"><label>Prazo Entrega (dias)</label><p-inputNumber [(ngModel)]="form.deliveryDays" styleClass="w-full" /></div>
          <div class="col-4"><label>Dia Vencimento</label><p-inputNumber [(ngModel)]="form.paymentDueDay" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="save()" />
      </ng-template>
    </p-dialog>
  `,
})
export class SupplierListComponent implements OnInit {
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  suppliers = signal<any[]>([]);
  loading = signal(true);
  showNew = false;
  form: any = {};
  filterText = '';
  categories = ['MATERIAL', 'SERVICO', 'EQUIPAMENTO', 'MÃO DE OBRA'].map(c => ({ label: c, value: c }));

  ngOnInit() {
    this.http.get<any>('/suppliers?size=100').subscribe({ next: r => { this.suppliers.set(r.content || r || []); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  edit(s: any) { this.form = { ...s }; this.showNew = true; }

  filter(event: any) { /* PrimeNG global filter handles it */ }

  save() {
    if (this.form.id) {
      this.http.put(`/suppliers/${this.form.id}`, this.form).subscribe({ next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Atualizado' }); this.ngOnInit(); } });
    } else {
      this.http.post('/suppliers', this.form).subscribe({ next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Fornecedor criado' }); this.ngOnInit(); } });
    }
  }
}
