import { Component, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'empty-state',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, RouterLink],
  template: `
    <div class="empty-state">
      <mat-icon class="empty-icon">{{ icon() }}</mat-icon>
      <h3 class="empty-title">{{ title() }}</h3>
      <p class="empty-message">{{ message() }}</p>
      @if (actionLabel() && actionRoute()) {
        <a mat-flat-button [routerLink]="actionRoute()">{{ actionLabel() }}</a>
      }
    </div>
  `,
  styles: `
    :host {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 100%;
      padding: 48px 16px;
    }

    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      max-width: 400px;
      gap: 8px;
    }

    .empty-icon {
      font-size: 64px;
      width: 64px;
      height: 64px;
      color: var(--mat-sys-outline);
      margin-bottom: 8px;
    }

    .empty-title {
      margin: 0;
      font-size: 1.25rem;
      font-weight: 500;
      color: var(--mat-sys-on-surface);
    }

    .empty-message {
      margin: 0 0 16px;
      font-size: 0.875rem;
      color: var(--mat-sys-on-surface-variant);
      line-height: 1.5;
    }
  `,
})
export class EmptyStateComponent {
  icon = input.required<string>();
  title = input.required<string>();
  message = input.required<string>();
  actionLabel = input<string>();
  actionRoute = input<string>();
}
