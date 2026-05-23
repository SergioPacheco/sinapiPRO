import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { errorInterceptor } from './error-interceptor';

describe('ErrorInterceptor', () => {
  let httpMock: HttpTestingController;
  let http: HttpClient;
  let router: Router;
  let snackBar: MatSnackBar;
  const emptyFn = () => {};

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    httpMock = TestBed.inject(HttpTestingController);
    http = TestBed.inject(HttpClient);
    router = TestBed.inject(Router);
    snackBar = TestBed.inject(MatSnackBar);
  });

  afterEach(() => httpMock.verify());

  it('should redirect to login on 401', () => {
    spyOn(router, 'navigateByUrl');
    spyOn(snackBar, 'open');

    http.get('/api/user').subscribe({ next: emptyFn, error: emptyFn, complete: emptyFn });
    httpMock.expectOne('/api/user').flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(router.navigateByUrl).toHaveBeenCalledWith('/auth/login');
    expect(snackBar.open).not.toHaveBeenCalled();
  });

  it('should show connection error on status 0', () => {
    spyOn(snackBar, 'open');

    http.get('/api/user').subscribe({ next: emptyFn, error: emptyFn, complete: emptyFn });
    httpMock.expectOne('/api/user').error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });

    expect(snackBar.open).toHaveBeenCalledWith(
      'Sem conexão com o servidor. Verifique sua internet.',
      'Fechar',
      { duration: 5000 }
    );
  });

  it('should show permission error on 403', () => {
    spyOn(snackBar, 'open');

    http.get('/api/user').subscribe({ next: emptyFn, error: emptyFn, complete: emptyFn });
    httpMock.expectOne('/api/user').flush({}, { status: 403, statusText: 'Forbidden' });

    expect(snackBar.open).toHaveBeenCalledWith(
      'Você não tem permissão para esta ação',
      'Fechar',
      { duration: 5000 }
    );
  });

  it('should show not found error on 404', () => {
    spyOn(snackBar, 'open');

    http.get('/api/user').subscribe({ next: emptyFn, error: emptyFn, complete: emptyFn });
    httpMock.expectOne('/api/user').flush({}, { status: 404, statusText: 'Not Found' });

    expect(snackBar.open).toHaveBeenCalledWith(
      'Recurso não encontrado',
      'Fechar',
      { duration: 5000 }
    );
  });

  it('should show internal error on 500', () => {
    spyOn(snackBar, 'open');

    http.get('/api/user').subscribe({ next: emptyFn, error: emptyFn, complete: emptyFn });
    httpMock.expectOne('/api/user').flush({}, { status: 500, statusText: 'Internal Server Error' });

    expect(snackBar.open).toHaveBeenCalledWith(
      'Erro interno do servidor. Tente novamente mais tarde.',
      'Fechar',
      { duration: 5000 }
    );
  });

  it('should show ProblemDetail message for other 4xx errors', () => {
    spyOn(snackBar, 'open');

    http.get('/api/user').subscribe({ next: emptyFn, error: emptyFn, complete: emptyFn });
    httpMock.expectOne('/api/user').flush(
      { detail: 'Campo inválido', title: 'Validation Error' },
      { status: 422, statusText: 'Unprocessable Entity' }
    );

    expect(snackBar.open).toHaveBeenCalledWith(
      'Campo inválido',
      'Fechar',
      { duration: 5000 }
    );
  });

  it('should fallback to generic message when no ProblemDetail body', () => {
    spyOn(snackBar, 'open');

    http.get('/api/user').subscribe({ next: emptyFn, error: emptyFn, complete: emptyFn });
    httpMock.expectOne('/api/user').flush(null, { status: 409, statusText: 'Conflict' });

    expect(snackBar.open).toHaveBeenCalledWith(
      'Erro 409: Conflict',
      'Fechar',
      { duration: 5000 }
    );
  });
});
