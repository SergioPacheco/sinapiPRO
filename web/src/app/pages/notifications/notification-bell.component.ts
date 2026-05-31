import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';

interface NotifItem { id: string; title: string; message: string; severity: string; type: string; read: boolean; createdAt: string; }

@Component({
  selector: 'app-notification-bell',
  standalone: true,
  template: `
    <div class="bell-wrapper">
      <button class="bell-btn" (click)="open = !open">
        <i class="pi pi-bell"></i>
        @if (unreadCount() > 0) {
          <span class="badge">{{ unreadCount() > 9 ? '9+' : unreadCount() }}</span>
        }
      </button>
      @if (open) {
        <div class="dropdown">
          <div class="dropdown-header">
            <strong>Notificações</strong>
            @if (unreadCount() > 0) {
              <button class="mark-all" (click)="markAllRead()">Marcar todas lidas</button>
            }
          </div>
          <div class="dropdown-body">
            @for (n of notifications(); track n.id) {
              <div class="notif-item" [class.unread]="!n.read" (click)="markRead(n.id)">
                <span class="notif-icon" [attr.data-severity]="n.severity">●</span>
                <div class="notif-content">
                  <span class="notif-title">{{ n.title }}</span>
                  <span class="notif-msg">{{ n.message }}</span>
                </div>
              </div>
            } @empty {
              <div class="empty">Nenhuma notificação</div>
            }
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .bell-wrapper { position: relative; }
    .bell-btn { background: none; border: none; cursor: pointer; color: var(--sp-text-muted, #8a8aaa); font-size: 1.1rem; position: relative; padding: 0.4rem; }
    .badge { position: absolute; top: -2px; right: -4px; background: #ef5350; color: #fff; font-size: 0.6rem; padding: 1px 4px; border-radius: 8px; font-weight: 700; }
    .dropdown { position: absolute; top: 100%; right: 0; width: 320px; background: #16213e; border: 1px solid #2a2a4a; border-radius: 10px; box-shadow: 0 8px 24px rgba(0,0,0,0.4); z-index: 1000; overflow: hidden; }
    .dropdown-header { display: flex; justify-content: space-between; align-items: center; padding: 0.75rem 1rem; border-bottom: 1px solid #2a2a4a;
      strong { font-size: 0.85rem; color: #e0e0e0; }
      .mark-all { background: none; border: none; color: #4fc3f7; font-size: 0.75rem; cursor: pointer; } }
    .dropdown-body { max-height: 300px; overflow-y: auto; }
    .notif-item { display: flex; gap: 0.5rem; padding: 0.75rem 1rem; cursor: pointer; border-bottom: 1px solid #1a1a2e;
      &:hover { background: #1a2744; }
      &.unread { background: #0f1e36; } }
    .notif-icon { font-size: 0.6rem; margin-top: 0.3rem;
      &[data-severity="WARNING"] { color: #ffa726; }
      &[data-severity="CRITICAL"] { color: #ef5350; }
      &[data-severity="INFO"] { color: #4fc3f7; } }
    .notif-content { display: flex; flex-direction: column; gap: 0.15rem; }
    .notif-title { font-size: 0.8rem; color: #e0e0e0; }
    .notif-msg { font-size: 0.7rem; color: #8a8aaa; }
    .empty { padding: 2rem; text-align: center; color: #6a6a8a; font-size: 0.85rem; }
  `]
})
export class NotificationBellComponent implements OnInit, OnDestroy {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private eventSource: EventSource | null = null;

  open = false;
  notifications = signal<NotifItem[]>([]);
  unreadCount = signal(0);

  ngOnInit() {
    this.loadNotifications();
    this.connectSSE();
  }

  ngOnDestroy() {
    this.eventSource?.close();
  }

  private loadNotifications() {
    this.http.get<any>('/notifications?size=10').subscribe(res => {
      const items = res.content || res;
      this.notifications.set(items);
      this.unreadCount.set(items.filter((n: NotifItem) => !n.read).length);
    });
  }

  private connectSSE() {
    const token = this.auth.getToken?.() || localStorage.getItem('sp_token');
    if (!token) return;
    this.eventSource = new EventSource(`/api/v1/events/stream?token=${token}`);
    this.eventSource.onmessage = (event) => {
      const notif = JSON.parse(event.data) as NotifItem;
      this.notifications.update(list => [notif, ...list].slice(0, 20));
      this.unreadCount.update(c => c + 1);
    };
    this.eventSource.onerror = () => {
      this.eventSource?.close();
      setTimeout(() => this.connectSSE(), 5000); // reconnect
    };
  }

  markRead(id: string) {
    this.http.post(`/notifications/${id}/read`, {}).subscribe(() => {
      this.notifications.update(list => list.map(n => n.id === id ? { ...n, read: true } : n));
      this.unreadCount.update(c => Math.max(0, c - 1));
    });
  }

  markAllRead() {
    this.http.post('/notifications/read-all', {}).subscribe(() => {
      this.notifications.update(list => list.map(n => ({ ...n, read: true })));
      this.unreadCount.set(0);
    });
  }
}
