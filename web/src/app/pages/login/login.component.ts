import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, InputTextModule, PasswordModule, ButtonModule],
  template: `
    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-brand">🏗️ <span>SinapiPRO</span></div>
        <p class="text-muted" style="text-align:center;margin-bottom:1.5rem">Gestão completa de obras</p>
        <div class="flex flex-column gap-3">
          <div class="flex flex-column gap-1">
            <label for="email">E-mail</label>
            <input pInputText id="email" [(ngModel)]="email" placeholder="seu@email.com" />
          </div>
          <div class="flex flex-column gap-1">
            <label for="password">Senha</label>
            <p-password id="password" [(ngModel)]="password" [feedback]="false" [toggleMask]="true" styleClass="w-full" inputStyleClass="w-full" />
          </div>
          <p-button label="Entrar" icon="pi pi-sign-in" [loading]="loading()" (onClick)="login()" styleClass="w-full" />
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-wrapper {
      height: 100vh; display: flex; align-items: center; justify-content: center;
      background: var(--sp-surface);
    }
    .login-card {
      width: 380px; padding: 2.5rem;
      background: var(--sp-surface-card); border: 1px solid var(--sp-border);
      border-radius: 12px;
    }
    .login-brand {
      text-align: center; font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem;
      span { margin-left: 0.5rem; }
    }
  `],
})
export class LoginComponent {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private router = inject(Router);

  email = '';
  password = '';
  loading = signal(false);

  login() {
    this.loading.set(true);
    this.http.post<any>('/auth/login', { email: this.email, password: this.password }).subscribe({
      next: res => {
        this.auth.login(res.token, res.user);
        this.router.navigate(['/dashboard']);
      },
      error: () => this.loading.set(false),
    });
  }
}
