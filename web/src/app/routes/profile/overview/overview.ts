import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { TranslateModule } from '@ngx-translate/core';
import { AuthService } from '@core/authentication';

@Component({
  selector: 'app-profile-overview',
  templateUrl: './overview.html',
  styleUrl: './overview.scss',
  imports: [MatIconModule, MatDividerModule, TranslateModule],
})
export class ProfileOverview {
  private readonly authService = inject(AuthService);
  private readonly authUser = toSignal(this.authService.user(), { initialValue: {} });

  readonly user = computed(() => {
    const current = this.authUser() as Record<string, unknown>;
    const roles = Array.isArray(current.roles) ? current.roles.filter(Boolean).map(String) : [];

    return {
      name: typeof current.name === 'string' && current.name ? current.name : 'Usuário',
      email: typeof current.email === 'string' && current.email ? current.email : '-',
      role: roles.join(', ') || 'Sem papel',
      memberSince: 'Janeiro 2026',
      language: 'Português (BR)',
    };
  });
}
