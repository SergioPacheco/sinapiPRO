import { HttpErrorResponse, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from '@core/authentication';
import { catchError, throwError } from 'rxjs';
import { BASE_URL, hasHttpScheme } from './base-url-interceptor';

export function tokenInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn) {
  const router = inject(Router);
  const baseUrl = inject(BASE_URL, { optional: true });
  const tokenService = inject(TokenService);

  const includeBaseUrl = (url: string) => {
    if (!baseUrl) return false;
    return new RegExp(`^${baseUrl.replace(/\/$/, '')}`, 'i').test(url);
  };

  const shouldAppendToken = (url: string) => !hasHttpScheme(url) || includeBaseUrl(url);

  // Never add token to auth endpoints (login, refresh)
  const isAuthRequest = (url: string) => url.includes('/auth/token') || url.includes('/auth/login');

  if (tokenService.valid() && shouldAppendToken(req.url) && !isAuthRequest(req.url)) {
    return next(
      req.clone({
        headers: req.headers.append('Authorization', tokenService.getBearerToken()),
        withCredentials: true,
      })
    ).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          tokenService.clear();
        }
        return throwError(() => error);
      })
    );
  }

  return next(req);
}
