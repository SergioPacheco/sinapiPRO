import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-profile-overview',
  templateUrl: './overview.html',
  styleUrl: './overview.scss',
  imports: [MatIconModule, MatDividerModule],
})
export class ProfileOverview {
  user = {
    name: 'SinapiPRO Admin',
    email: 'admin@sinapipro.dev',
    role: 'Administrador',
    memberSince: 'Janeiro 2026',
    language: 'Português (BR)',
  };
}
