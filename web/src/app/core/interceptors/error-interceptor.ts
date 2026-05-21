import { HttpErrorResponse, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';

export function errorInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn) {
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        router.navigateByUrl('/auth/login');
      } else if (error.status === 0) {
        showSnackBar(snackBar, 'Sem conexão com o servidor. Verifique sua internet.');
      } else if (error.status === 403) {
        showSnackBar(snackBar, 'Você não tem permissão para esta ação');
      } else if (error.status === 404) {
        showSnackBar(snackBar, 'Recurso não encontrado');
      } else if (error.status >= 500) {
        showSnackBar(snackBar, 'Erro interno do servidor. Tente novamente mais tarde.');
      } else {
        const msg = extractMessage(error);
        showSnackBar(snackBar, msg);
      }
      return throwError(() => error);
    })
  );
}

function showSnackBar(snackBar: MatSnackBar, message: string): void {
  snackBar.open(message, 'Fechar', { duration: 5000 });
}

function extractMessage(error: HttpErrorResponse): string {
  const body = error.error;
  if (body?.detail) return body.detail;
  if (body?.title) return body.title;
  if (body?.message) return body.message;
  return `Erro ${error.status}: ${error.statusText}`;
}
