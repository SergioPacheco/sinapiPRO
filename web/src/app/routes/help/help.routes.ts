import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { Routes } from '@angular/router';
import { PageHeader } from '@shared';
import { HelpService, HelpSection } from '@shared';

@Component({
  selector: 'app-help-page',
  standalone: true,
  imports: [FormsModule, MatButtonModule, MatCardModule, MatIconModule, MatFormFieldModule, MatInputModule, MatListModule, PageHeader],
  template: `
    <page-header title="Documentação" subtitle="Guia completo do SinapiPRO" />

    <div class="help-layout">
      <!-- Sidebar Index -->
      <nav class="help-index">
        <mat-form-field appearance="outline" class="search-field">
          <mat-icon matPrefix>search</mat-icon>
          <input matInput [(ngModel)]="searchQuery" (ngModelChange)="onSearch()" placeholder="Buscar..." />
        </mat-form-field>

        <mat-nav-list>
          @for (section of displayedSections; track section.id) {
            <a mat-list-item (click)="select(section)" [class.active]="selected()?.id === section.id">
              <mat-icon matListItemIcon>{{ section.icon }}</mat-icon>
              <span matListItemTitle>{{ section.title }}</span>
            </a>
          }
        </mat-nav-list>
      </nav>

      <!-- Content -->
      <main class="help-main">
        @if (selected(); as section) {
          <mat-card>
            <mat-card-header>
              <mat-icon mat-card-avatar>{{ section.icon }}</mat-icon>
              <mat-card-title>{{ section.title }}</mat-card-title>
              <mat-card-subtitle>{{ section.summary }}</mat-card-subtitle>
            </mat-card-header>
            <mat-card-content>
              @for (block of section.content; track $index) {
                @if (block.type === 'paragraph') { <p class="help-text">{{ block.text }}</p> }
                @if (block.type === 'steps') {
                  <ol class="help-steps">
                    @for (item of block.items; track $index) { <li>{{ item }}</li> }
                  </ol>
                }
                @if (block.type === 'tip') {
                  <div class="callout tip"><mat-icon>lightbulb</mat-icon><span>{{ block.text }}</span></div>
                }
                @if (block.type === 'warning') {
                  <div class="callout warning"><mat-icon>warning</mat-icon><span>{{ block.text }}</span></div>
                }
                @if (block.type === 'shortcut') {
                  <div class="callout shortcut"><mat-icon>keyboard</mat-icon><span>{{ block.text }}</span></div>
                }
              }
            </mat-card-content>
          </mat-card>
        }
      </main>
    </div>
  `,
  styles: `
    .help-layout { display: grid; grid-template-columns: 280px 1fr; gap: 24px; min-height: 70vh; }
    @media (max-width: 768px) { .help-layout { grid-template-columns: 1fr; } }
    .help-index { position: sticky; top: 16px; align-self: start; }
    .search-field { width: 100%; }
    .help-index a.active { background: var(--mat-sys-surface-container-highest); border-radius: 8px; }
    .help-main mat-card { padding: 24px; }
    .help-text { font-size: 15px; line-height: 1.7; margin: 12px 0; }
    .help-steps { padding-left: 24px; margin: 12px 0; }
    .help-steps li { margin: 8px 0; font-size: 15px; line-height: 1.6; }
    .callout { display: flex; align-items: flex-start; gap: 10px; padding: 12px 16px; border-radius: 8px; margin: 12px 0; font-size: 14px; line-height: 1.5; }
    .callout mat-icon { font-size: 20px; width: 20px; height: 20px; margin-top: 2px; flex-shrink: 0; }
    .callout.tip { background: rgba(76,175,80,.08); color: #2e7d32; }
    .callout.warning { background: rgba(255,152,0,.08); color: #e65100; }
    .callout.shortcut { background: rgba(33,150,243,.08); color: #1565c0; }
  `,
})
export class HelpPageComponent {
  private readonly helpService = inject(HelpService);

  allSections = this.helpService.getAll();
  displayedSections = this.allSections;
  selected = signal<HelpSection | null>(this.allSections[0]);
  searchQuery = '';

  select(section: HelpSection) { this.selected.set(section); }

  onSearch() {
    this.displayedSections = this.searchQuery
      ? this.helpService.search(this.searchQuery)
      : this.allSections;
    if (this.displayedSections.length > 0 && !this.displayedSections.includes(this.selected()!)) {
      this.selected.set(this.displayedSections[0]);
    }
  }
}

export const routes: Routes = [{ path: '', component: HelpPageComponent }];
