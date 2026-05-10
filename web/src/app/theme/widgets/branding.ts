import { Component, input } from '@angular/core';

@Component({
  selector: 'app-branding',
  template: `
    <a class="branding" href="/">
      <svg class="branding-logo" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
        <rect width="32" height="32" rx="8" fill="#1e3a5f"/>
        <path d="M8 24V12l8-5 8 5v12" stroke="#60a5fa" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M12 24v-6h8v6" stroke="#60a5fa" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M16 7v4" stroke="#60a5fa" stroke-width="2" stroke-linecap="round"/>
      </svg>
      @if (showName()) {
        <span class="branding-name">SinapiPRO</span>
      }
    </a>
  `,
  styles: `
    .branding {
      display: flex;
      align-items: center;
      margin: 0 0.5rem;
      text-decoration: none;
      white-space: nowrap;
      color: inherit;
      border-radius: 50rem;
    }
    .branding-logo {
      width: 2rem;
      height: 2rem;
    }
    .branding-name {
      margin: 0 0.5rem;
      font-size: 1rem;
      font-weight: 600;
      color: var(--mat-sys-on-surface);
    }
  `,
})
export class Branding {
  readonly showName = input(true);
}
