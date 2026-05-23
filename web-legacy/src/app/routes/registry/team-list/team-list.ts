import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MtxDialog } from '@ng-matero/extensions/dialog';
import { PageHeader } from '@shared';
import { Team } from '../models/registry.model';
import { RegistryService } from '../services/registry.service';

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
            <mat-card-subtitle>
              {{ team.projectName || 'Sem projeto vinculado' }} • {{ team.members.length }} membros
            </mat-card-subtitle>
          </mat-card-header>
          <mat-card-content>
            @if (team.description) {
              <p class="team-description">{{ team.description }}</p>
            }
            <mat-chip-set>
              @for (m of team.members; track m.name) {
                <mat-chip>{{ m.name }} ({{ m.role }})</mat-chip>
              }
            </mat-chip-set>
            <div class="team-actions">
              <button mat-button type="button" (click)="edit(team.id)">Editar</button>
              <button mat-button color="warn" type="button" (click)="remove(team)">Inativar</button>
            </div>
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
    .team-description { margin: 0 0 12px; color: var(--mat-sys-on-surface-variant); }
    .team-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 12px; }
  `,
})
export class TeamListComponent implements OnInit {
  private readonly service = inject(RegistryService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MtxDialog);
  teams = signal<Team[]>([]);
  loading = signal(true);

  ngOnInit() {
    this.service.listTeams().subscribe({
      next: list => { this.teams.set(list); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  create() { this.router.navigate(['/registry/teams/new']); }

  edit(id: string) { this.router.navigate(['/registry/teams', id, 'edit']); }

  remove(team: Team) {
    this.dialog.confirm('Confirmar inativação', `Inativar a equipe "${team.name}"?`, () =>
      this.service.deleteTeam(team.id).subscribe(() => {
        this.teams.update(list => list.filter(item => item.id !== team.id));
      })
    );
  }
}
