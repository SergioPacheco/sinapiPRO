import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [TagModule],
  template: `
    <h3 style="margin:0 0 1rem">Meu Perfil</h3>
    @if (auth.user(); as u) {
      <div class="grid">
        <div class="col-12 md:col-6"><label class="text-muted">Nome</label><p style="font-size:1.1rem;margin:0.25rem 0 1rem">{{ u.name }}</p></div>
        <div class="col-12 md:col-6"><label class="text-muted">Email</label><p style="font-size:1.1rem;margin:0.25rem 0 1rem">{{ u.email }}</p></div>
        <div class="col-12">
          <label class="text-muted">Perfis</label>
          <div class="flex gap-2 mt-1">@for (r of u.roles; track r) { <p-tag [value]="r" /> }</div>
        </div>
      </div>
    }
  `,
})
export class ProfileComponent {
  auth = inject(AuthService);
}
