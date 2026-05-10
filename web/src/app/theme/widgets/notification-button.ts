import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { NotificationService, Notification } from '@core/services/notification.service';

@Component({
  selector: 'app-notification',
  template: `
    <button matIconButton [matMenuTriggerFor]="menu">
      @if (svc.unreadCount() > 0) {
        <mat-icon [matBadge]="svc.unreadCount()" matBadgeColor="warn">notifications</mat-icon>
      } @else {
        <mat-icon>notifications_none</mat-icon>
      }
    </button>

    <mat-menu #menu="matMenu" class="notification-menu">
      <div class="notif-header" (click)="$event.stopPropagation()">
        <strong>Notificações</strong>
        <span class="notif-count">{{ svc.unreadCount() }} não lidas</span>
      </div>
      <mat-divider />
      @for (n of svc.notifications().slice(0, 8); track n.id) {
        <button mat-menu-item (click)="open(n)" [class.unread]="!n.read">
          <mat-icon [class]="'severity-' + n.severity">{{ iconFor(n.type) }}</mat-icon>
          <span class="notif-content">
            <span class="notif-title">{{ n.title }}</span>
            <span class="notif-time">{{ n.createdAt | date:'short' }}</span>
          </span>
        </button>
      }
      @if (svc.notifications().length === 0) {
        <div class="notif-empty">Nenhuma notificação</div>
      }
    </mat-menu>
  `,
  styles: `
    .notif-header { padding: 12px 16px; display: flex; justify-content: space-between; align-items: center; }
    .notif-count { font-size: 12px; color: var(--mat-sys-on-surface-variant); }
    .notif-content { display: flex; flex-direction: column; margin-left: 8px; }
    .notif-title { font-size: 13px; white-space: normal; line-height: 1.3; }
    .notif-time { font-size: 11px; color: var(--mat-sys-on-surface-variant); }
    .unread { font-weight: 500; }
    .notif-empty { padding: 24px; text-align: center; color: var(--mat-sys-on-surface-variant); font-size: 13px; }
    .severity-CRITICAL { color: #ef4444; }
    .severity-WARNING { color: #f59e0b; }
    .severity-INFO { color: #3b82f6; }
    :host ::ng-deep .mat-badge-content { --mat-badge-background-color: #ef4444; --mat-badge-text-color: #fff; }
  `,
  imports: [MatBadgeModule, MatButtonModule, MatIconModule, MatMenuModule, MatDividerModule],
})
export class NotificationButton implements OnInit {
  readonly svc = inject(NotificationService);
  private readonly router = inject(Router);

  ngOnInit() { this.svc.init(); }

  iconFor(type: string): string {
    const map: Record<string, string> = {
      MEASUREMENT_SUBMITTED: 'straighten',
      STOCK_LOW: 'inventory',
      RFI_OVERDUE: 'help_outline',
      SYSTEM: 'info',
    };
    return map[type] || 'notifications';
  }

  open(n: Notification) {
    this.svc.markAsRead(n.id);
    // Navigate based on entity type if available
    if (n.entityType === 'MEASUREMENT') {
      this.router.navigate(['/projects']);
    }
  }
}
