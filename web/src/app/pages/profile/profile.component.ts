import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule, InputTextModule, ButtonModule],
  template: `
    <h2 style="margin:0 0 1.5rem;color:var(--sp-text)">Meu Perfil</h2>

    <div class="profile-card">
      <div class="avatar-large">{{ initials() }}</div>
      <div class="flex flex-column gap-3" style="flex:1">
        <div class="field"><label>Nome</label><input pInputText [(ngModel)]="name" class="w-full" /></div>
        <div class="field"><label>E-mail</label><input pInputText [(ngModel)]="email" class="w-full" /></div>
        <div class="field"><label>Cargo</label><input pInputText [(ngModel)]="role" class="w-full" /></div>
        <div class="field"><label>Telefone</label><input pInputText [(ngModel)]="phone" class="w-full" /></div>
        <p-button label="Salvar" icon="pi pi-save" (onClick)="save()" />
      </div>
    </div>
  `,
  styles: [`
    .profile-card { display: flex; gap: 24px; align-items: flex-start; background: var(--sp-surface-card); border: 1px solid var(--sp-border); border-radius: 8px; padding: 24px; max-width: 500px; }
    .avatar-large { width: 64px; height: 64px; border-radius: 50%; background: var(--sp-primary); color: white; display: flex; align-items: center; justify-content: center; font-size: 22px; font-weight: 700; }
    .field { margin-bottom: 4px; }
    .field label { display: block; font-size: 11px; color: var(--sp-text-muted); text-transform: uppercase; margin-bottom: 4px; }
  `],
})
export class ProfileComponent {
  auth = inject(AuthService);
  private messages = inject(MessageService);

  name = this.auth.user()?.name || '';
  email = this.auth.user()?.email || '';
  role = 'Engenheiro Civil';
  phone = '';

  initials() { return this.name.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase(); }

  save() { this.messages.add({ severity: 'success', summary: 'Perfil atualizado' }); }
}
