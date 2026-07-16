import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { SidebarComponent } from './sidebar.component';
import { TopbarComponent } from './topbar.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, TopbarComponent],
  template: `
    <a class="skip-link" href="#main-content">Ir para conteúdo principal</a>
    <div class="layout-wrapper" [class.sidebar-collapsed]="collapsed">
      <app-sidebar
        [collapsed]="collapsed"
        [obras]="obras()"
        [obraId]="obraId"
        (toggleCollapse)="collapsed = !collapsed"
        (obraChange)="onObraChange($event)" />
      <div class="layout-main">
        <app-topbar [obraNome]="obraNome()" />
        <main id="main-content" class="layout-content" tabindex="-1">
          <router-outlet />
        </main>
      </div>
    </div>
  `,
  styles: [`
    .skip-link { position:absolute; top:-100%; left:0; background:var(--sp-primary); color:white; padding:8px 16px; z-index:9999; font-size:14px; font-weight:600; border-radius:0 0 6px 0; transition:top 0.2s; }
    .skip-link:focus { top:0; }
    .sidebar-collapsed .layout-sidebar { width: 52px; overflow: hidden; transition: width 0.2s cubic-bezier(0.4, 0, 0.2, 1); }
    .sidebar-collapsed .layout-sidebar .sidebar-nav .nav-item span,
    .sidebar-collapsed .layout-sidebar .nav-section,
    .sidebar-collapsed .layout-sidebar .obra-selector,
    .sidebar-collapsed .layout-sidebar .sidebar-footer { opacity: 0; pointer-events: none; position: absolute; transition: opacity 0.1s ease; }
    .sidebar-collapsed .nav-item { justify-content:center; padding:0.5rem; }
    .sidebar-collapsed .nav-item i { margin:0; }
  `],
})
export class LayoutComponent implements OnInit {
  private http = inject(HttpClient);

  obras = signal<any[]>([]);
  obraId: string | null = null;
  collapsed = false;

  ngOnInit() {
    this.http.get<any>('/projects?page=0&size=50').subscribe({
      next: res => this.obras.set(res.content || res),
    });
    this.obraId = localStorage.getItem('selectedObraId');
  }

  onObraChange(id: string | null) {
    this.obraId = id;
    if (id) {
      localStorage.setItem('selectedObraId', id);
    } else {
      localStorage.removeItem('selectedObraId');
    }
  }

  obraNome(): string {
    const obra = this.obras().find(o => o.id === this.obraId);
    return obra?.name || '';
  }
}
