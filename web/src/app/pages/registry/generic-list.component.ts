import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-generic-list',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h3 style="margin:0;color:var(--sp-text)">{{ title }}</h3>
      <p-button label="Novo" icon="pi pi-plus" size="small" (onClick)="showDialog = true; editing = {}" />
    </div>

    <p-table [value]="items()" styleClass="p-datatable-sm p-datatable-gridlines" [paginator]="true" [rows]="15" [rowHover]="true">
      <ng-template pTemplate="header"><tr><th>Nome</th><th>Documento</th><th>Telefone</th><th>Email</th><th>Cidade/UF</th><th style="width:80px"></th></tr></ng-template>
      <ng-template pTemplate="body" let-item><tr>
        <td>{{item.name}}</td>
        <td style="font-size:0.8rem">{{item.document}}</td>
        <td style="font-size:0.8rem">{{item.phone || item.cellPhone}}</td>
        <td style="font-size:0.8rem">{{item.email}}</td>
        <td style="font-size:0.8rem">{{item.city}} {{item.state ? '/ ' + item.state : ''}}</td>
        <td>
          <p-button icon="pi pi-pencil" [text]="true" size="small" (onClick)="edit(item)" />
          <p-button icon="pi pi-trash" [text]="true" size="small" severity="danger" (onClick)="remove(item.id)" />
        </td>
      </tr></ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhum registro encontrado</td></tr></ng-template>
    </p-table>

    <p-dialog [header]="editing?.id ? 'Editar' : 'Novo ' + title" [(visible)]="showDialog" [modal]="true" [style]="{width:'500px'}">
      <div class="flex flex-column gap-3 pt-2">
        <input pInputText [(ngModel)]="editing.name" placeholder="Nome *" class="w-full" />
        <input pInputText [(ngModel)]="editing.document" placeholder="CPF/CNPJ" class="w-full" />
        <div class="flex gap-2">
          <input pInputText [(ngModel)]="editing.phone" placeholder="Telefone" class="flex-1" />
          <input pInputText [(ngModel)]="editing.whatsapp" placeholder="WhatsApp" class="flex-1" />
        </div>
        <input pInputText [(ngModel)]="editing.email" placeholder="Email" class="w-full" />
        <div class="flex gap-2">
          <input pInputText [(ngModel)]="editing.city" placeholder="Cidade" class="flex-1" />
          <input pInputText [(ngModel)]="editing.state" placeholder="UF" style="width:60px" />
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" [text]="true" (onClick)="showDialog = false" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="save()" />
      </ng-template>
    </p-dialog>
  `,
})
export class GenericListComponent implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);

  items = signal<any[]>([]);
  entity = '';
  title = '';
  showDialog = false;
  editing: any = {};

  ngOnInit() {
    this.entity = this.route.snapshot.data['entity'];
    this.title = this.route.snapshot.data['title'];
    this.load();
  }

  load() {
    this.http.get<any[]>(`/registry/${this.entity}`).subscribe(r => this.items.set(r || []));
  }

  edit(item: any) { this.editing = { ...item }; this.showDialog = true; }

  save() {
    const url = `/registry/${this.entity}`;
    const req = this.editing.id
      ? this.http.put(`${url}/${this.editing.id}`, this.editing)
      : this.http.post(url, this.editing);
    req.subscribe({ next: () => { this.showDialog = false; this.load(); } });
  }

  remove(id: string) {
    this.http.delete(`/registry/${this.entity}/${id}`).subscribe(() => this.load());
  }
}
