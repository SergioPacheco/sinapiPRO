import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { filter, Subscription } from 'rxjs';
import { HelpService, HelpSection, HelpBlock } from '../../services/help.service';

@Component({
  selector: 'app-help-drawer',
  standalone: true,
  imports: [FormsModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule, MatListModule],
  template: `
    <div class="help-drawer" [class.open]="isOpen">
      <div class="help-header">
        <h3><mat-icon>help_outline</mat-icon> Ajuda</h3>
        <button mat-icon-button (click)="close()"><mat-icon>close</mat-icon></button>
      </div>

      <div class="help-search">
        <mat-form-field appearance="outline" class="full-width">
          <mat-icon matPrefix>search</mat-icon>
          <input matInput [(ngModel)]="searchQuery" (ngModelChange)="onSearch()" placeholder="Buscar na documentação..." />
        </mat-form-field>
      </div>

      <div class="help-content">
        @if (currentSection && !searchQuery) {
          <div class="section">
            <div class="section-header">
              <mat-icon>{{ currentSection.icon }}</mat-icon>
              <h4>{{ currentSection.title }}</h4>
            </div>
            <p class="section-summary">{{ currentSection.summary }}</p>
            @for (block of currentSection.content; track $index) {
              <div class="block" [attr.data-type]="block.type">
                @if (block.type === 'paragraph') { <p>{{ block.text }}</p> }
                @if (block.type === 'steps') {
                  <ol>@for (item of block.items; track $index) { <li>{{ item }}</li> }</ol>
                }
                @if (block.type === 'tip') { <div class="callout tip"><mat-icon>lightbulb</mat-icon><span>{{ block.text }}</span></div> }
                @if (block.type === 'warning') { <div class="callout warning"><mat-icon>warning</mat-icon><span>{{ block.text }}</span></div> }
                @if (block.type === 'shortcut') { <div class="callout shortcut"><mat-icon>keyboard</mat-icon><span>{{ block.text }}</span></div> }
              </div>
            }
          </div>

          <mat-nav-list class="section-nav">
            <h5>Outros tópicos</h5>
            @for (s of otherSections; track s.id) {
              <a mat-list-item (click)="openSection(s)">
                <mat-icon matListItemIcon>{{ s.icon }}</mat-icon>
                <span matListItemTitle>{{ s.title }}</span>
              </a>
            }
          </mat-nav-list>
        }

        @if (searchQuery && searchResults.length > 0) {
          <mat-nav-list>
            @for (s of searchResults; track s.id) {
              <a mat-list-item (click)="openSection(s)">
                <mat-icon matListItemIcon>{{ s.icon }}</mat-icon>
                <span matListItemTitle>{{ s.title }}</span>
                <span matListItemLine>{{ s.summary }}</span>
              </a>
            }
          </mat-nav-list>
        }

        @if (searchQuery && searchResults.length === 0) {
          <p class="no-results">Nenhum resultado para "{{ searchQuery }}"</p>
        }
      </div>

      <div class="help-footer">
        <a mat-button routerLink="/help" (click)="close()"><mat-icon>menu_book</mat-icon> Ver documentação completa</a>
      </div>
    </div>
  `,
  styles: `
    .help-drawer { position: fixed; top: 0; right: -400px; width: 380px; height: 100vh; background: var(--mat-sys-surface); border-left: 1px solid var(--mat-sys-outline-variant); z-index: 1000; display: flex; flex-direction: column; transition: right 0.25s ease; box-shadow: -4px 0 24px rgba(0,0,0,0.1); }
    .help-drawer.open { right: 0; }
    .help-header { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid var(--mat-sys-outline-variant); }
    .help-header h3 { margin: 0; display: flex; align-items: center; gap: 8px; font-size: 16px; }
    .help-search { padding: 12px 16px 0; }
    .full-width { width: 100%; }
    .help-content { flex: 1; overflow-y: auto; padding: 16px; }
    .section-header { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
    .section-header h4 { margin: 0; font-size: 18px; }
    .section-header mat-icon { color: var(--mat-sys-primary); }
    .section-summary { color: var(--mat-sys-on-surface-variant); margin-bottom: 16px; }
    .block p { margin: 8px 0; font-size: 14px; line-height: 1.6; }
    .block ol { padding-left: 20px; margin: 8px 0; }
    .block ol li { margin: 6px 0; font-size: 14px; line-height: 1.5; }
    .callout { display: flex; align-items: flex-start; gap: 8px; padding: 10px 12px; border-radius: 8px; margin: 8px 0; font-size: 13px; }
    .callout mat-icon { font-size: 18px; width: 18px; height: 18px; margin-top: 1px; }
    .callout.tip { background: rgba(76,175,80,.08); color: #2e7d32; }
    .callout.warning { background: rgba(255,152,0,.08); color: #e65100; }
    .callout.shortcut { background: rgba(33,150,243,.08); color: #1565c0; }
    .section-nav { margin-top: 24px; }
    .section-nav h5 { margin: 0 0 8px; font-size: 12px; text-transform: uppercase; color: var(--mat-sys-outline); letter-spacing: 0.5px; }
    .no-results { text-align: center; color: var(--mat-sys-on-surface-variant); padding: 24px; }
    .help-footer { padding: 12px 16px; border-top: 1px solid var(--mat-sys-outline-variant); }
  `,
})
export class HelpDrawerComponent implements OnInit, OnDestroy {
  private readonly helpService = inject(HelpService);
  private readonly router = inject(Router);
  private routerSub!: Subscription;
  private keyListener!: (e: KeyboardEvent) => void;

  isOpen = false;
  currentSection: HelpSection | null = null;
  otherSections: HelpSection[] = [];
  searchQuery = '';
  searchResults: HelpSection[] = [];

  ngOnInit() {
    this.routerSub = this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe((e: any) => {
      if (this.isOpen) this.loadContextual(e.urlAfterRedirects);
    });

    this.keyListener = (e: KeyboardEvent) => {
      if (e.key === 'F1') { e.preventDefault(); this.toggle(); }
      if (e.key === 'Escape' && this.isOpen) this.close();
    };
    document.addEventListener('keydown', this.keyListener);
  }

  ngOnDestroy() {
    this.routerSub?.unsubscribe();
    document.removeEventListener('keydown', this.keyListener);
  }

  toggle() {
    this.isOpen = !this.isOpen;
    if (this.isOpen) this.loadContextual(this.router.url);
  }

  open() {
    this.isOpen = true;
    this.loadContextual(this.router.url);
  }

  close() { this.isOpen = false; }

  openSection(section: HelpSection) {
    this.currentSection = section;
    this.searchQuery = '';
    this.otherSections = this.helpService.getAll().filter(s => s.id !== section.id);
  }

  onSearch() {
    this.searchResults = this.searchQuery ? this.helpService.search(this.searchQuery) : [];
  }

  private loadContextual(url: string) {
    this.currentSection = this.helpService.getByRoute(url) || this.helpService.getById('getting-started')!;
    this.otherSections = this.helpService.getAll().filter(s => s.id !== this.currentSection!.id);
  }
}
