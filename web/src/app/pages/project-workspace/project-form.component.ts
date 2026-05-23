import { Component, inject, signal, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ButtonModule } from 'primeng/button';
import { TextareaModule } from 'primeng/textarea';
import { MessageService, MenuItem } from 'primeng/api';
import { InlineCreateDrawerComponent, WizardStepperComponent } from '../../shared/components';

@Component({
  selector: 'app-project-form',
  standalone: true,
  imports: [FormsModule, InputTextModule, CalendarModule, DropdownModule, AutoCompleteModule, ButtonModule, TextareaModule, InlineCreateDrawerComponent, WizardStepperComponent],
  template: `
    <h3 style="margin:0 0 1rem">Nova Obra</h3>
    <sp-wizard #wizard [steps]="steps" finishLabel="Criar Obra" (finish)="save()" [finishing]="saving()">
      @if (wizard.activeIndex() === 0) {
        <div class="grid">
          <div class="col-12 md:col-4"><label>Código</label><input pInputText [(ngModel)]="form.code" class="w-full" /></div>
          <div class="col-12 md:col-8"><label>Nome</label><input pInputText [(ngModel)]="form.name" class="w-full" /></div>
          <div class="col-12"><label>Descrição</label><textarea pTextarea [(ngModel)]="form.description" rows="3" class="w-full"></textarea></div>
          <div class="col-12 md:col-6"><label>Início</label><p-calendar [(ngModel)]="form.startDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
          <div class="col-12 md:col-6"><label>Término</label><p-calendar [(ngModel)]="form.endDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
        </div>
      }
      @if (wizard.activeIndex() === 1) {
        <div class="grid">
          <div class="col-12"><label>Cliente</label><p-autoComplete [(ngModel)]="form.client" [suggestions]="clientSuggestions()" (completeMethod)="searchClients($event)" field="name" styleClass="w-full" placeholder="Buscar cliente..." /></div>
          <div class="col-12"><a class="cursor-pointer text-primary" (click)="drawer.open()"><i class="pi pi-plus mr-1"></i>Cadastrar novo cliente</a></div>
        </div>
      }
      @if (wizard.activeIndex() === 2) {
        <div class="grid">
          <div class="col-12 md:col-6"><label>Estado</label><p-dropdown [(ngModel)]="form.state" [options]="states" styleClass="w-full" placeholder="Selecione" /></div>
          <div class="col-12 md:col-6"><label>Regime de Contrato</label><p-dropdown [(ngModel)]="form.contractRegime" [options]="regimes" styleClass="w-full" placeholder="Selecione" /></div>
        </div>
      }
    </sp-wizard>

    <sp-drawer #drawer header="Novo Cliente" (save)="saveClient()">
      <div class="flex flex-column gap-3">
        <div><label>Nome</label><input pInputText [(ngModel)]="newClient.name" class="w-full" /></div>
        <div><label>CPF/CNPJ</label><input pInputText [(ngModel)]="newClient.document" class="w-full" /></div>
        <div><label>Email</label><input pInputText [(ngModel)]="newClient.email" class="w-full" /></div>
      </div>
    </sp-drawer>
  `,
})
export class ProjectFormComponent {
  @ViewChild('drawer') drawer!: InlineCreateDrawerComponent;
  private router = inject(Router);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  saving = signal(false);
  form: any = { code: '', name: '', description: '', startDate: null, endDate: null, client: null, state: null, contractRegime: null };
  newClient: any = {};
  clientSuggestions = signal<any[]>([]);

  steps: MenuItem[] = [{ label: 'Dados' }, { label: 'Cliente' }, { label: 'Configuração' }];
  states = ['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'].map(s => ({ label: s, value: s }));
  regimes = [{ label: 'Preço Global', value: 'GLOBAL_PRICE' }, { label: 'Preço Unitário', value: 'UNIT_PRICE' }, { label: 'Administração', value: 'ADMINISTRATION' }, { label: 'Empreitada Mista', value: 'MIXED' }];

  searchClients(event: any) {
    this.http.get<any[]>(`/clients?search=${encodeURIComponent(event.query)}`).subscribe(res => this.clientSuggestions.set(res));
  }

  saveClient() {
    this.http.post<any>('/clients', this.newClient).subscribe({
      next: res => { this.form.client = res; this.drawer.close(); this.messages.add({ severity: 'success', summary: 'Cliente criado' }); },
    });
  }

  save() {
    this.saving.set(true);
    const body = { ...this.form, clientId: this.form.client?.id };
    this.http.post<any>('/projects', body).subscribe({
      next: res => { this.messages.add({ severity: 'success', summary: 'Obra criada' }); this.router.navigate(['/projects', res.id]); },
      error: () => this.saving.set(false),
    });
  }
}
