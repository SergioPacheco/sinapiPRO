import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-lang-switcher',
  standalone: true,
  template: `
    <div class="lang-switcher">
      @for (lang of languages; track lang.code) {
        <button
          [class.active]="currentLang === lang.code"
          (click)="switchLang(lang.code)"
          [title]="lang.name">
          {{ lang.flag }}
        </button>
      }
    </div>
  `,
  styles: [`
    .lang-switcher { display: flex; gap: 4px; }
    button { background: none; border: 1px solid transparent; border-radius: 4px;
             font-size: 1.2rem; cursor: pointer; padding: 2px 6px; transition: all 0.2s; }
    button:hover { border-color: var(--sp-border); }
    button.active { border-color: var(--sp-primary); background: var(--sp-primary-alpha); }
  `],
})
export class LangSwitcherComponent {
  languages = [
    { code: 'pt-BR', name: 'Português', flag: '🇧🇷' },
    { code: 'en', name: 'English', flag: '🇺🇸' },
    { code: 'es', name: 'Español', flag: '🇪🇸' },
  ];

  currentLang: string;

  constructor(private translate: TranslateService) {
    this.currentLang = translate.currentLang || translate.defaultLang;
  }

  switchLang(lang: string) {
    this.currentLang = lang;
    this.translate.use(lang);
    localStorage.setItem('lang', lang);
  }
}
