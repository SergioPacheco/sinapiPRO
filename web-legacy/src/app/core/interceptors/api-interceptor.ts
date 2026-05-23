import { HttpHandlerFn, HttpRequest } from '@angular/common/http';

export function apiInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn) {
  return next(req);
}
