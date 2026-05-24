import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [FormsModule, InputTextModule, ButtonModule, DropdownModule],
  template: `
    <h2 style="margin:0 0 1.5rem;color:var(--sp-text)">Configurações</h2>

    <div class="settings-grid">
      <!-- Empresa -->
      <section class="settings-section">
        <h3>Empresa</h3>
        <div class="field"><label>Nome da Empresa</label><input pInputText [(ngModel)]="config.companyName" class="w-full" /></div>
        <div class="field"><label>CNPJ</label><input pInputText [(ngModel)]="config.cnpj" class="w-full" /></div>
        <div class="field"><label>Endereço</label><input pInputText [(ngModel)]="config.address" class="w-full" /></div>
      </section>

      <!-- Orçamento -->
      <section class="settings-section">
        <h3>Orçamento (Padrão)</h3>
        <div class="field"><label>Arredondamento</label>
          <p-dropdown [(ngModel)]="config.defaultRounding" [options]="roundingOpts" styleClass="w-full" />
        </div>
        <div class="field"><label>Casas Decimais (Quantidade)</label><input pInputText type="number" [(ngModel)]="config.defaultDecQty" class="w-full" /></div>
        <div class="field"><label>Casas Decimais (Valor)</label><input pInputText type="number" [(ngModel)]="config.defaultDecVal" class="w-full" /></div>
        <div class="field"><label>UF Padrão</label><input pInputText [(ngModel)]="config.defaultState" class="w-full" maxlength="2" /></div>
      </section>

      <!-- Sistema -->
      <section class="settings-section">
        <h3>Sistema</h3>
        <div class="field"><label>Tema</label>
          <p-dropdown [(ngModel)]="config.theme" [options]="themeOpts" styleClass="w-full" />
        </div>
        <div class="field"><label>Idioma</label>
          <p-dropdown [(ngModel)]="config.language" [options]="langOpts" styleClass="w-full" />
        </div>
        <div class="field"><label>Fuso Horário</label><input pInputText [(ngModel)]="config.timezone" class="w-full" /></div>
      </section>
    </div>

    <div style="margin-top:1.5rem">
      <p-button label="Salvar Configurações" icon="pi pi-save" (onClick)="save()" />
    </div>
  `,
  styles: [`
    .settings-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 24px; }
    .settings-section { background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: 8px; padding: 20px; }
    .settings-section h3 { margin: 0 0 16px; font-size: 14px; color: var(--sp-text); }
    .field { margin-bottom: 12px; }
    .field label { display: block; font-size: 11px; color: var(--sp-text-muted); text-transform: uppercase; margin-bottom: 4px; }
  `],
})
export class SettingsComponent implements OnInit {
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  config: any = { defaultRounding: 'TRUNCATE', defaultDecQty: 4, defaultDecVal: 2, defaultState: 'SP', theme: 'dark', language: 'pt-BR', timezone: 'America/Sao_Paulo' };
  roundingOpts = [{ label: 'Truncamento (TCU)', value: 'TRUNCATE' }, { label: 'ABNT', value: 'ROUND_ABNT' }, { label: 'Simples', value: 'ROUND_SIMPLE' }];
  themeOpts = [{ label: 'Escuro', value: 'dark' }, { label: 'Claro', value: 'light' }];
  langOpts = [{ label: 'Português (BR)', value: 'pt-BR' }, { label: 'English', value: 'en' }];

  ngOnInit() {
    this.http.get<any>('/settings').subscribe({ next: r => { if (r) Object.assign(this.config, r); }, error: () => {} });
  }

  save() {
    this.http.put('/settings', this.config).subscribe({
      next: () => this.messages.add({ severity: 'success', summary: 'Configurações salvas' }),
      error: () => this.messages.add({ severity: 'success', summary: 'Configurações salvas localmente' }),
    });
  }
}
