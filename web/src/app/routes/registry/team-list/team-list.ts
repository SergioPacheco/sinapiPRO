import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { PageHeader } from '@shared';

interface Team {
  id: string; name: string; description: string; members: { name: string; role: string }[];
}

@Component({
  selector: 'app-team-list',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatCardModule, MatChipsModule, PageHeader],
  template: `
    <page-header title="Equipes" subtitle="Composição de equipes de obra">
      <button mat-flat-button color="primary" (click)="create()"><mat-icon>add</mat-icon> Nova Equipe</button>
    </page-header>
    <div class="teams-grid">
      @for (team of teams(); track team.id) {
        <mat-card>
          <mat-card-header>
            <mat-card-title>{{ team.name }}</mat-card-title>
            <mat-card-subtitle>{{ team.description || team.members.length + ' membros' }}</mat-card-subtitle>
          </mat-card-header>
          <mat-card-content>
            <mat-chip-set>
              @for (m of team.members; track m.name) {
                <mat-chip>{{ m.name }} ({{ m.role }})</mat-chip>
              }
            </mat-chip-set>
          </mat-card-content>
        </mat-card>
      }
    </div>
    @if (teams().length === 0 && !loading()) {
      <p class="empty">Nenhuma equipe cadastrada.</p>
    }
  `,
  styles: `
    .teams-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; }
    .empty { text-align: center; padding: 40px; color: var(--mat-sys-on-surface-variant); }
  `,
})
export class TeamListComponent implements OnInit {
  private readonly http = inject(HttpClient);
  teams = signal<Team[]>([]);
  loading = signal(true);

  ngOnInit() {
    this.http.get<Team[]>('/teams').subscribe({
      next: list => { this.teams.set(list); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  create() { /* TODO: open dialog */ }
}
