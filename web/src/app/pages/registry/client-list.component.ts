import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Clientes</h2>
      <p-button label="Novo Cliente" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <p-table [value]="clients()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="15">
      <ng-template pTemplate="caption">
        <input pInputText [(ngModel)]="filterText" placeholder="Buscar cliente..." style="width:250px" />
      </ng-template>
      <ng-template pTemplate="header">
        <tr>
          <th>Nome / Razão Social</th>
          <th style="width:130px">CPF/CNPJ</th>
          <th style="width:180px">Email</th>
          <th style="width:120px">Telefone</th>
          <th style="width:120px">Cidade/UF</th>
          <th style="width:50px"></th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-c>
        <tr>
          <td>{{ c.name }}</td>
          <td class="font-mono" style="font-size:0.8rem">{{ c.document }}</td>
          <td style="font-size:0.85rem;color:var(--sp-text-muted)">{{ c.email }}</td>
          <td style="font-size:0.85rem">{{ c.phone }}</td>
          <td style="font-size:0.85rem">{{ c.city }}/{{ c.state }}</td>
          <td><p-button icon="pi pi-pencil" [text]="true" size="small" (onClick)="edit(c)" /></td>
        </tr>
      </ng-template>
    </p-table>

    <!-- Novo/Editar Cliente -->
    <p-dialog [header]="form.id ? 'Editar Cliente' : 'Novo Cliente'" [(visible)]="showNew" [style]="{width:'520px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div class="grid">
          <div class="col-8"><label>Nome / Razão Social</label><input pInputText [(ngModel)]="form.name" class="w-full" /></div>
          <div class="col-4"><label>CPF/CNPJ</label><input pInputText [(ngModel)]="form.document" class="w-full" /></div>
        </div>
        <div class="grid">
          <div class="col-6"><label>Email</label><input pInputText [(ngModel)]="form.email" class="w-full" /></div>
          <div class="col-6"><label>Telefone</label><input pInputText [(ngModel)]="form.phone" class="w-full" /></div>
        </div>
        <div class="grid">
          <div class="col-8"><label>Endereço</label><input pInputText [(ngModel)]="form.address" class="w-full" /></div>
          <div class="col-2"><label>Cidade</label><input pInputText [(ngModel)]="form.city" class="w-full" /></div>
          <div class="col-2"><label>UF</label><input pInputText [(ngModel)]="form.state" class="w-full" maxlength="2" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="save()" />
      </ng-template>
    </p-dialog>
  `,
})
export class ClientListComponent implements OnInit {
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  clients = signal<any[]>([]);
  loading = signal(true);
  showNew = false;
  form: any = {};
  filterText = '';

  ngOnInit() {
    this.http.get<any>('/registry/clients?size=100').subscribe({ next: r => { this.clients.set(r.content || r || []); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  edit(c: any) { this.form = { ...c }; this.showNew = true; }

  save() {
    if (this.form.id) {
      this.http.put(`/registry/clients/${this.form.id}`, this.form).subscribe({ next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Atualizado' }); this.ngOnInit(); } });
    } else {
      this.http.post('/registry/clients', this.form).subscribe({ next: () => { this.showNew = false; this.messages.add({ severity: 'success', summary: 'Cliente criado' }); this.ngOnInit(); } });
    }
  }
}
