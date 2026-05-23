import { HttpInterceptorFn } from '@angular/common/http';

export const baseUrlInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.startsWith('/')) {
    return next(req.clone({ url: `/api/v1${req.url}` }));
  }
  return next(req);
};
