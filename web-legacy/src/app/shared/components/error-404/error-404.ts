import { Component, ViewEncapsulation } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'error-404',
  imports: [MatIconModule, MatButtonModule, RouterLink],
  encapsulation: ViewEncapsulation.None,
  template: `
    <div class="error-404-wrap">
      <mat-icon class="error-404-icon">search_off</mat-icon>
      <h2 class="error-404-title">Página não encontrada</h2>
      <p class="error-404-message">
        A página que você está procurando não existe ou foi movida.
      </p>
      <a mat-flat-button routerLink="/dashboard">Voltar ao Dashboard</a>
    </div>
  `,
  styles: `
    .error-404-wrap {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 60vh;
      text-align: center;
      padding: 2rem;
    }

    .error-404-icon {
      font-size: 5rem;
      width: 5rem;
      height: 5rem;
      color: var(--mat-sys-outline);
      margin-bottom: 1.5rem;
    }

    .error-404-title {
      margin: 0 0 0.5rem;
      font-size: 1.5rem;
      font-weight: 500;
      color: var(--mat-sys-on-surface);
    }

    .error-404-message {
      margin: 0 0 1.5rem;
      font-size: 1rem;
      color: var(--mat-sys-on-surface-variant);
      max-width: 400px;
    }
  `,
})
export class Error404Component {}
