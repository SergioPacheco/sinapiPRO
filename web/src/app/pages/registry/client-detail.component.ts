import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TabViewModule } from 'primeng/tabview';
import { CheckboxModule } from 'primeng/checkbox';

@Component({
  selector: 'app-client-detail',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, TabViewModule, CheckboxModule],
  template: `
    @if (client(); as c) {
      <h3 style="margin:0 0 1rem">{{ c.name }}</h3>
      <p class="text-muted">{{ c.document }} | {{ c.email }} | {{ c.phone }}</p>
      <p-tabView>
          <p-tabPanel header="Contatos">
            <div class="flex justify-content-end mb-2"><p-button label="Novo Contato" icon="pi pi-plus" size="small" (onClick)="showCreate = true" /></div>
            <p-table [value]="contacts()" styleClass="p-datatable-sm">
              <ng-template pTemplate="header"><tr><th>Nome</th><th>Cargo</th><th>Email</th><th>Telefone</th><th>Depto</th><th style="width:70px">Principal</th></tr></ng-template>
              <ng-template pTemplate="body" let-r>
                <tr><td>{{ r.name }}</td><td>{{ r.role }}</td><td>{{ r.email }}</td><td>{{ r.phone }}</td><td>{{ r.department }}</td><td>{{ r.isPrimary ? '✓' : '' }}</td></tr>
              </ng-template>
              <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center text-muted p-3">Nenhum contato</td></tr></ng-template>
            </p-table>
          </p-tabPanel>
      </p-tabView>
    }

    <p-dialog header="Novo Contato" [(visible)]="showCreate" [style]="{width:'450px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Nome</label><input pInputText [(ngModel)]="form.name" class="w-full" /></div>
        <div><label>Cargo</label><input pInputText [(ngModel)]="form.role" class="w-full" /></div>
        <div><label>Email</label><input pInputText [(ngModel)]="form.email" class="w-full" /></div>
        <div><label>Telefone</label><input pInputText [(ngModel)]="form.phone" class="w-full" /></div>
        <div><label>Departamento</label><input pInputText [(ngModel)]="form.department" class="w-full" /></div>
        <div><p-checkbox [(ngModel)]="form.isPrimary" [binary]="true" label="Contato principal" /></div>
      </div>
      <ng-template pTemplate="footer"><p-button label="Salvar" icon="pi pi-check" (onClick)="create()" /></ng-template>
    </p-dialog>
  `,
})
export class ClientDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  client = signal<any>(null);
  contacts = signal<any[]>([]);
  showCreate = false;
  form: any = { name: '', role: '', email: '', phone: '', department: '', isPrimary: false };

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('clientId');
    this.http.get<any>(`/registry/clients/${id}`).subscribe(c => this.client.set(c));
    this.loadContacts(id!);
  }

  loadContacts(id: string) {
    this.http.get<any>(`/registry/clients/${id}/contacts`).subscribe(res => this.contacts.set(res.content || res));
  }

  create() {
    const id = this.route.snapshot.paramMap.get('clientId');
    this.http.post(`/registry/clients/${id}/contacts`, this.form).subscribe(() => {
      this.showCreate = false; this.form = { name: '', role: '', email: '', phone: '', department: '', isPrimary: false };
      this.loadContacts(id!);
    });
  }
}
