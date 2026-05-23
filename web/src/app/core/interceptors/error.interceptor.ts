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
      const body = err.error;
      // Extract the most useful error message
      let detail = '';
      if (body) {
        if (body.violations?.length) {
          // Bean Validation errors (list of field violations)
          detail = body.violations.map((v: any) => `${v.field}: ${v.message}`).join('\n');
        } else if (body.errors?.length) {
          detail = body.errors.map((e: any) => typeof e === 'string' ? e : `${e.field || ''}: ${e.defaultMessage || e.message || ''}`).join('\n');
        } else if (body.detail && body.detail !== 'Erro interno do servidor') {
          detail = body.detail;
        } else if (body.message) {
          detail = body.message;
        } else if (typeof body === 'string') {
          detail = body;
        }
      }
      if (!detail) detail = err.statusText || 'Erro desconhecido';

      if (err.status === 401) {
        router.navigate(['/login']);
      } else if (err.status === 403) {
        messages.add({ severity: 'warn', summary: 'Sem permissão', detail, life: 6000 });
      } else if (err.status === 400) {
        messages.add({ severity: 'warn', summary: 'Dados inválidos', detail, life: 8000 });
      } else if (err.status === 404) {
        messages.add({ severity: 'info', summary: 'Não encontrado', detail, life: 5000 });
      } else if (err.status === 409) {
        messages.add({ severity: 'warn', summary: 'Conflito', detail, life: 6000 });
      } else if (err.status >= 500) {
        messages.add({ severity: 'error', summary: 'Erro no servidor', detail: detail || 'Erro interno — verifique os logs', life: 8000 });
      } else if (err.status > 0) {
        messages.add({ severity: 'error', summary: `Erro (${err.status})`, detail, life: 6000 });
      } else {
        messages.add({ severity: 'error', summary: 'Sem conexão', detail: 'Verifique sua internet', life: 5000 });
      }
      return throwError(() => err);
    })
  );
};
