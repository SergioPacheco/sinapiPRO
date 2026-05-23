import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [FormsModule, CardModule, TableModule, ButtonModule, InputTextModule],
  template: `
    <h3 style="margin:0 0 1rem">Configurações</h3>
    <div class="grid">
      <div class="col-12 md:col-6">
        <p-card header="Perfil da Empresa">
          <div class="flex flex-column gap-3">
            <div><label>Nome</label><input pInputText [(ngModel)]="company.name" class="w-full" /></div>
            <div><label>CNPJ</label><input pInputText [(ngModel)]="company.cnpj" class="w-full" /></div>
            <div><label>Logo (URL)</label><input pInputText [(ngModel)]="company.logo" class="w-full" /></div>
            <p-button label="Salvar" icon="pi pi-check" size="small" (onClick)="saveCompany()" [loading]="savingCompany()" />
          </div>
        </p-card>
      </div>
      <div class="col-12 md:col-6">
        <p-card header="Usuários / Perfis de Acesso">
          <p-table [value]="roles()" [loading]="loadingRoles()" styleClass="p-datatable-sm">
            <ng-template pTemplate="header"><tr><th>Nome</th><th>Descrição</th><th style="width:100px">Permissões</th></tr></ng-template>
            <ng-template pTemplate="body" let-r>
              <tr><td>{{ r.name }}</td><td>{{ r.description }}</td><td>{{ r.permissionsCount ?? r.permissions?.length ?? 0 }}</td></tr>
            </ng-template>
          </p-table>
        </p-card>
      </div>
    </div>
  `,
})
export class SettingsComponent implements OnInit {
  private http = inject(HttpClient);
  roles = signal<any[]>([]);
  loadingRoles = signal(true);
  savingCompany = signal(false);
  company: any = { name: '', cnpj: '', logo: '' };

  ngOnInit() {
    this.http.get<any>('/roles').subscribe({
      next: res => { this.roles.set(res.content || res); this.loadingRoles.set(false); },
      error: () => this.loadingRoles.set(false),
    });
    this.http.get<any>('/registry/company').subscribe({ next: res => this.company = res, error: () => {} });
  }

  saveCompany() {
    this.savingCompany.set(true);
    this.http.put('/registry/company', this.company).subscribe({
      next: () => this.savingCompany.set(false),
      error: () => this.savingCompany.set(false),
    });
  }
}
