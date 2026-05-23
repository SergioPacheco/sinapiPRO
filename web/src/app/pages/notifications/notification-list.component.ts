import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [ButtonModule, TagModule],
  template: `
    <h3 style="margin:0 0 1rem">Notificações</h3>
    @for (n of items(); track n.id) {
      <div class="notification-item" [class.unread]="!n.read">
        <div class="flex align-items-center gap-2">
          <p-tag [value]="n.severity" [severity]="sevMap(n.severity)" />
          <strong>{{ n.title }}</strong>
          <span class="text-muted text-sm ml-auto">{{ n.createdAt }}</span>
        </div>
        <p class="mt-1 mb-0">{{ n.message }}</p>
        @if (!n.read) {
          <p-button label="Marcar como lida" [text]="true" size="small" icon="pi pi-check" (onClick)="markRead(n)" class="mt-1" />
        }
      </div>
    } @empty {
      <p class="text-muted text-center p-4">Nenhuma notificação</p>
    }
  `,
  styles: [`.notification-item { padding:1rem; border-bottom:1px solid var(--sp-border); } .unread { background:var(--sp-surface-hover); }`],
})
export class NotificationListComponent implements OnInit {
  private http = inject(HttpClient);
  items = signal<any[]>([]);

  ngOnInit() { this.load(); }

  load() {
    this.http.get<any>('/notifications').subscribe(res => this.items.set(res.content || res));
  }

  markRead(n: any) {
    this.http.post(`/notifications/${n.id}/read`, {}).subscribe(() => {
      n.read = true;
      this.items.update(list => [...list]);
    });
  }

  sevMap(s: string) { return ({ INFO: 'info', WARN: 'warn', ERROR: 'danger', SUCCESS: 'success' } as any)[s] || 'secondary'; }
}
