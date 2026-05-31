import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TabViewModule } from 'primeng/tabview';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { MultiSelectModule } from 'primeng/multiselect';
import { CheckboxModule } from 'primeng/checkbox';
import { ChipModule } from 'primeng/chip';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [FormsModule, TabViewModule, TableModule, ButtonModule, DialogModule, InputTextModule, MultiSelectModule, CheckboxModule, ChipModule],
  template: `
    <h2 style="margin:0 0 1rem;color:var(--sp-text)">Configurações</h2>

    <p-tabView>
      <p-tabPanel header="Empresa">
        <div class="grid" style="max-width:600px">
          <div class="col-12"><label class="block text-sm text-color-secondary mb-1">Razão Social</label><input pInputText [(ngModel)]="company.name" class="w-full" /></div>
          <div class="col-6"><label class="block text-sm text-color-secondary mb-1">CNPJ</label><input pInputText [(ngModel)]="company.cnpj" class="w-full" /></div>
          <div class="col-6"><label class="block text-sm text-color-secondary mb-1">Inscrição Estadual</label><input pInputText [(ngModel)]="company.stateRegistration" class="w-full" /></div>
          <div class="col-12"><label class="block text-sm text-color-secondary mb-1">Endereço</label><input pInputText [(ngModel)]="company.address" class="w-full" /></div>
          <div class="col-4"><label class="block text-sm text-color-secondary mb-1">Cidade</label><input pInputText [(ngModel)]="company.city" class="w-full" /></div>
          <div class="col-2"><label class="block text-sm text-color-secondary mb-1">UF</label><input pInputText [(ngModel)]="company.state" class="w-full" /></div>
          <div class="col-3"><label class="block text-sm text-color-secondary mb-1">CEP</label><input pInputText [(ngModel)]="company.zipCode" class="w-full" /></div>
          <div class="col-3"><label class="block text-sm text-color-secondary mb-1">Telefone</label><input pInputText [(ngModel)]="company.phone" class="w-full" /></div>
          <div class="col-6"><label class="block text-sm text-color-secondary mb-1">Email</label><input pInputText [(ngModel)]="company.email" class="w-full" /></div>
          <div class="col-6"><label class="block text-sm text-color-secondary mb-1">Site</label><input pInputText [(ngModel)]="company.website" class="w-full" /></div>
          <div class="col-12 mt-3"><p-button label="Salvar" icon="pi pi-check" (onClick)="saveCompany()" /></div>
        </div>
      </p-tabPanel>

      <p-tabPanel header="Usuários">
        <div class="flex justify-content-end mb-2">
          <p-button label="Novo Usuário" icon="pi pi-user-plus" size="small" (onClick)="showUserDialog = true; userEditing = {}" />
        </div>
        <p-table [value]="users()" styleClass="p-datatable-sm p-datatable-gridlines" [paginator]="true" [rows]="15">
          <ng-template pTemplate="header"><tr><th>Nome</th><th>Email</th><th>Perfis</th><th>Obras</th><th>Ativo</th><th style="width:80px"></th></tr></ng-template>
          <ng-template pTemplate="body" let-u><tr>
            <td>{{u.name}}</td>
            <td style="font-size:0.8rem">{{u.email}}</td>
            <td>@for(r of u.roles; track r){<p-chip [label]="r" styleClass="mr-1" />}</td>
            <td style="font-size:0.8rem">{{u.projectAccess?.length ? u.projectAccess.length + ' obras' : 'Todas'}}</td>
            <td><i [class]="u.active ? 'pi pi-check-circle text-green-500' : 'pi pi-times-circle text-red-500'"></i></td>
            <td><p-button icon="pi pi-pencil" [text]="true" size="small" (onClick)="editUser(u)" /></td>
          </tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Perfis (Roles)">
        <div class="flex justify-content-end mb-2">
          <p-button label="Novo Perfil" icon="pi pi-plus" size="small" (onClick)="showRoleDialog = true; roleEditing = {permissions:[]}" />
          <p-button label="Carregar Padrões" icon="pi pi-download" size="small" severity="secondary" class="ml-2" (onClick)="initDefaults()" />
        </div>
        <p-table [value]="roles()" styleClass="p-datatable-sm p-datatable-gridlines">
          <ng-template pTemplate="header"><tr><th>Nome</th><th>Descrição</th><th>Permissões</th><th style="width:60px"></th></tr></ng-template>
          <ng-template pTemplate="body" let-r><tr>
            <td style="font-weight:600">{{r.name}}</td>
            <td style="font-size:0.85rem">{{r.description}}</td>
            <td style="font-size:0.8rem">{{r.permissionsCount}} permissões</td>
            <td><p-button icon="pi pi-pencil" [text]="true" size="small" (onClick)="editRole(r)" /></td>
          </tr></ng-template>
        </p-table>
      </p-tabPanel>

      <p-tabPanel header="Permissões">
        @for(group of permissionGroups(); track group.module) {
          <div class="mb-3">
            <strong style="font-size:12px;color:var(--sp-text-muted)">{{group.module}}</strong>
            <div class="flex flex-wrap gap-1 mt-1">
              @for(p of group.permissions; track p) { <p-chip [label]="p" styleClass="text-xs" /> }
            </div>
          </div>
        }
      </p-tabPanel>
    </p-tabView>

    <p-dialog header="Usuário" [(visible)]="showUserDialog" [modal]="true" [style]="{width:'500px'}">
      <div class="flex flex-column gap-3 pt-2">
        <input pInputText [(ngModel)]="userEditing.name" placeholder="Nome *" class="w-full" />
        <input pInputText [(ngModel)]="userEditing.email" placeholder="Email *" class="w-full" />
        <p-multiSelect [options]="roles()" [(ngModel)]="userEditing.roleIds" optionLabel="name" optionValue="id" placeholder="Perfis" styleClass="w-full" />
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" [text]="true" (onClick)="showUserDialog = false" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="saveUser()" />
      </ng-template>
    </p-dialog>

    <p-dialog header="Perfil" [(visible)]="showRoleDialog" [modal]="true" [style]="{width:'600px'}">
      <div class="flex flex-column gap-3 pt-2">
        <input pInputText [(ngModel)]="roleEditing.name" placeholder="Nome do perfil *" class="w-full" />
        <input pInputText [(ngModel)]="roleEditing.description" placeholder="Descrição" class="w-full" />
        <div style="max-height:300px;overflow-y:auto">
          @for(group of permissionGroups(); track group.module) {
            <div class="mb-2">
              <strong style="font-size:11px">{{group.module}}</strong>
              <div class="flex flex-wrap gap-2 mt-1">
                @for(p of group.permissions; track p) {
                  <label class="flex align-items-center gap-1" style="font-size:11px">
                    <p-checkbox [(ngModel)]="roleEditing.permissions" [value]="p" /><span>{{p.split('.')[1]}}</span>
                  </label>
                }
              </div>
            </div>
          }
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Salvar" icon="pi pi-check" (onClick)="saveRole()" />
      </ng-template>
    </p-dialog>
  `,
  styles: [`:host ::ng-deep .p-chip { font-size: 10px !important; }`]
})
export class SettingsComponent {
  private http = inject(HttpClient);
  users = signal<any[]>([]);
  roles = signal<any[]>([]);
  permissionGroups = signal<any[]>([]);
  showUserDialog = false; userEditing: any = {};
  showRoleDialog = false; roleEditing: any = { permissions: [] };
  company: any = {};

  ngOnInit() {
    this.http.get<any[]>('/users').subscribe({ next: r => this.users.set(r || []), error: () => {} });
    this.http.get<any[]>('/roles').subscribe({ next: r => this.roles.set(r || []), error: () => {} });
    this.http.get<any[]>('/roles/permissions').subscribe({ next: r => this.permissionGroups.set(r || []), error: () => {} });
    this.http.get<any>('/settings/company').subscribe({ next: r => this.company = r || {}, error: () => {} });
  }

  editUser(u: any) { this.userEditing = { ...u, roleIds: [] }; this.showUserDialog = true; }
  editRole(r: any) { this.roleEditing = { ...r, permissions: [...(r.permissions || [])] }; this.showRoleDialog = true; }

  saveUser() {
    const req = this.userEditing.id
      ? this.http.put(`/users/${this.userEditing.id}/roles`, this.userEditing.roleIds || [])
      : this.http.post('/users', this.userEditing);
    req.subscribe(() => { this.showUserDialog = false; this.ngOnInit(); });
  }

  saveRole() {
    const req = this.roleEditing.id
      ? this.http.put(`/roles/${this.roleEditing.id}`, this.roleEditing)
      : this.http.post('/roles', this.roleEditing);
    req.subscribe(() => { this.showRoleDialog = false; this.ngOnInit(); });
  }

  initDefaults() {
    this.http.post<any[]>('/roles/initialize-defaults', {}).subscribe(() => this.ngOnInit());
  }

  saveCompany() {
    this.http.put('/settings/company', this.company).subscribe();
  }
}
