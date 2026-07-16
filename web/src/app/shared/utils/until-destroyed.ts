import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MonoTypeOperatorFunction } from 'rxjs';

/**
 * RxJS operator that auto-unsubscribes when the component/service is destroyed.
 * Uses Angular's DestroyRef (Angular 16+) — no manual unsubscribe needed.
 *
 * Usage:
 *   this.http.get('/api/data').pipe(untilDestroyed()).subscribe(...)
 *
 * Must be called inside an injection context (constructor, field initializer, or inject()).
 */
export function untilDestroyed<T>(): MonoTypeOperatorFunction<T> {
  return takeUntilDestroyed<T>(inject(DestroyRef));
}
