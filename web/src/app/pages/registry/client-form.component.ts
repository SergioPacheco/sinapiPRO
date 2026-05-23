import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [FormsModule, InputTextModule, DropdownModule, ButtonModule],
  template: `
    <h3 style="margin:0 0 1rem">Novo Cliente</h3>
    <div class="grid">
      <div class="col-12 md:col-8"><label>Nome</label><input pInputText [(ngModel)]="form.name" class="w-full" /></div>
      <div class="col-12 md:col-4"><label>CPF/CNPJ</label><input pInputText [(ngModel)]="form.document" class="w-full" /></div>
      <div class="col-12 md:col-6"><label>Email</label><input pInputText [(ngModel)]="form.email" class="w-full" /></div>
      <div class="col-12 md:col-6"><label>Telefone</label><input pInputText [(ngModel)]="form.phone" class="w-full" /></div>
      <div class="col-12"><label>Endereço</label><input pInputText [(ngModel)]="form.address" class="w-full" /></div>
      <div class="col-12 md:col-6"><label>Cidade</label><input pInputText [(ngModel)]="form.city" class="w-full" /></div>
      <div class="col-12 md:col-6"><label>Estado</label><p-dropdown [(ngModel)]="form.state" [options]="states" styleClass="w-full" placeholder="Selecione" /></div>
      <div class="col-12 flex gap-2">
        <p-button label="Salvar" icon="pi pi-check" (onClick)="save()" [loading]="saving()" />
        <p-button label="Cancelar" severity="secondary" (onClick)="router.navigate(['/registry/clients'])" />
      </div>
    </div>
  `,
})
export class ClientFormComponent {
  router = inject(Router);
  private http = inject(HttpClient);
  private messages = inject(MessageService);
  saving = signal(false);
  form: any = {};
  states = ['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'].map(s => ({ label: s, value: s }));

  save() {
    this.saving.set(true);
    this.http.post('/registry/clients', this.form).subscribe({
      next: () => { this.messages.add({ severity: 'success', summary: 'Cliente cadastrado' }); this.router.navigate(['/registry/clients']); },
      error: () => this.saving.set(false),
    });
  }
}
