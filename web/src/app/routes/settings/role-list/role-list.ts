import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { PageHeader } from '@shared';

interface Role {
  id: string; name: string; description: string; permissions: string[];
}

@Component({
  selector: 'app-role-list',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatChipsModule, MatCardModule, PageHeader],
  template: `
    <page-header title="Perfis e Permissões" subtitle="Gerencie os perfis de acesso do sistema">
      <button mat-flat-button color="primary" (click)="create()"><mat-icon>add</mat-icon> Novo Perfil</button>
    </page-header>

    <div class="roles-grid">
      @for (role of roles(); track role.id) {
        <mat-card class="role-card">
          <mat-card-header>
            <mat-card-title>{{ role.name }}</mat-card-title>
            <mat-card-subtitle>{{ role.description || 'Sem descrição' }}</mat-card-subtitle>
          </mat-card-header>
          <mat-card-content>
            <mat-chip-set>
              @for (p of role.permissions; track p) {
                <mat-chip>{{ p }}</mat-chip>
              }
            </mat-chip-set>
          </mat-card-content>
          <mat-card-actions align="end">
            <button mat-button><mat-icon>edit</mat-icon> Editar</button>
            <button mat-button color="warn"><mat-icon>delete</mat-icon></button>
          </mat-card-actions>
        </mat-card>
      }
    </div>

    @if (roles().length === 0 && !loading()) {
      <p class="empty">Nenhum perfil cadastrado. Crie o primeiro perfil.</p>
    }
  `,
  styles: `
    .roles-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(360px, 1fr)); gap: 16px; }
    .role-card mat-chip { font-size: 11px; }
    .empty { text-align: center; padding: 40px; color: var(--mat-sys-on-surface-variant); }
  `,
})
export class RoleListComponent implements OnInit {
  private readonly http = inject(HttpClient);
  roles = signal<Role[]>([]);
  loading = signal(true);

  ngOnInit() {
    this.http.get<Role[]>('/roles').subscribe({
      next: list => { this.roles.set(list); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  create() { /* TODO: open dialog */ }
}
