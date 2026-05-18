import { HttpErrorResponse, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { HotToastService } from '@ngxpert/hot-toast';
import { catchError, throwError } from 'rxjs';

export enum STATUS {
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  INTERNAL_SERVER_ERROR = 500,
}

export function errorInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn) {
  const router = inject(Router);
  const toast = inject(HotToastService);
  const errorPages = [STATUS.FORBIDDEN, STATUS.NOT_FOUND, STATUS.INTERNAL_SERVER_ERROR];

  const getMessage = (error: HttpErrorResponse) => {
    const body = error.error;
    // RFC 9457 ProblemDetail
    if (body?.detail) {
      if (body.violations?.length) {
        return body.violations.map((v: { field: string; message: string }) => `${v.field}: ${v.message}`).join(', ');
      }
      return body.detail;
    }
    if (body?.title) {
      return body.title;
    }
    if (body?.message) {
      return body.message;
    }
    return `${error.status} ${error.statusText}`;
  };

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === STATUS.UNAUTHORIZED) {
        router.navigateByUrl('/auth/login');
      } else if (!req.url.includes('/api/')) {
        // Only redirect to error pages for non-API navigation failures
        if (errorPages.includes(error.status)) {
          router.navigateByUrl(`/${error.status}`, { skipLocationChange: true });
        }
      } else {
        console.error('ERROR', error);
        toast.error(getMessage(error));
      }

      return throwError(() => error);
    })
  );
}
