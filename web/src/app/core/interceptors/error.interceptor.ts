import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const messages = inject(MessageService);
  const router = inject(Router);

  return next(req).pipe(
    catchError(err => {
      const detail = err.error?.detail || err.error?.message || err.statusText;
      if (err.status === 401) {
        router.navigate(['/login']);
      } else if (err.status === 403) {
        messages.add({ severity: 'warn', summary: 'Sem permissão', detail });
      } else if (err.status === 404) {
        messages.add({ severity: 'info', summary: 'Não encontrado', detail });
      } else if (err.status >= 500) {
        messages.add({ severity: 'error', summary: 'Erro interno', detail: 'Tente novamente em instantes' });
      } else if (err.status > 0) {
        messages.add({ severity: 'error', summary: 'Erro', detail });
      } else {
        messages.add({ severity: 'error', summary: 'Sem conexão', detail: 'Verifique sua internet' });
      }
      return throwError(() => err);
    })
  );
};
