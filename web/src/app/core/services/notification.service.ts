import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { interval, switchMap, tap, catchError, of } from 'rxjs';

export interface Notification {
  id: string;
  type: string;
  severity: string;
  title: string;
  message: string;
  entityType?: string;
  entityId?: string;
  read: boolean;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly http = inject(HttpClient);

  notifications = signal<Notification[]>([]);
  unreadCount = signal(0);

  init() {
    this.refresh();
    interval(30_000).pipe(
      switchMap(() => this.fetchCount()),
      catchError(() => of(null))
    ).subscribe();
  }

  refresh() {
    this.http.get<any>('/notifications').pipe(catchError(() => of(null))).subscribe(res => {
      if (!res) return;
      const list: Notification[] = Array.isArray(res) ? res : (res.content ?? []);
      this.notifications.set(list);
      this.unreadCount.set(list.filter(n => !n.read).length);
    });
  }

  markAsRead(id: string) {
    this.http.post(`/notifications/${id}/read`, {}).subscribe(() => {
      this.notifications.update(list => list.map(n => n.id === id ? { ...n, read: true } : n));
      this.unreadCount.update(c => Math.max(0, c - 1));
    });
  }

  private fetchCount() {
    return this.http.get<{ count: number }>('/notifications/count').pipe(
      tap(res => this.unreadCount.set(res.count)),
      catchError(() => of(null))
    );
  }
}
