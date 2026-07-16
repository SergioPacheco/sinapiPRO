import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { errorInterceptor } from './error.interceptor';

describe('errorInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let routerSpy: jest.Mocked<Router>;
  let messagesSpy: jest.Mocked<MessageService>;

  beforeEach(() => {
    routerSpy = { navigate: jest.fn() } as any;
    messagesSpy = { add: jest.fn() } as any;

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: routerSpy },
        { provide: MessageService, useValue: messagesSpy },
      ],
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should redirect to /login on 401', () => {
    // Arrange & Act
    http.get('/api/test').subscribe({ error: () => {} });
    httpMock.expectOne('/api/test').flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    // Assert
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should show warning toast on 403', () => {
    // Arrange & Act
    http.get('/api/test').subscribe({ error: () => {} });
    httpMock.expectOne('/api/test').flush({ detail: 'Acesso negado' }, { status: 403, statusText: 'Forbidden' });

    // Assert
    expect(messagesSpy.add).toHaveBeenCalledWith(
      expect.objectContaining({ severity: 'warn', summary: 'Sem permissão' })
    );
  });

  it('should show warning toast on 400 with validation errors', () => {
    // Arrange & Act
    http.post('/api/test', {}).subscribe({ error: () => {} });
    httpMock.expectOne('/api/test').flush(
      { violations: [{ field: 'name', message: 'não pode ser vazio' }] },
      { status: 400, statusText: 'Bad Request' }
    );

    // Assert
    expect(messagesSpy.add).toHaveBeenCalledWith(
      expect.objectContaining({ severity: 'warn', summary: 'Dados inválidos', detail: 'name: não pode ser vazio' })
    );
  });

  it('should show error toast on 500', () => {
    // Arrange & Act
    http.get('/api/test').subscribe({ error: () => {} });
    httpMock.expectOne('/api/test').flush({ message: 'NullPointerException' }, { status: 500, statusText: 'Internal Server Error' });

    // Assert
    expect(messagesSpy.add).toHaveBeenCalledWith(
      expect.objectContaining({ severity: 'error', summary: 'Erro no servidor' })
    );
  });

  it('should show info toast on 404', () => {
    // Arrange & Act
    http.get('/api/test').subscribe({ error: () => {} });
    httpMock.expectOne('/api/test').flush({ detail: 'Recurso não encontrado' }, { status: 404, statusText: 'Not Found' });

    // Assert
    expect(messagesSpy.add).toHaveBeenCalledWith(
      expect.objectContaining({ severity: 'info', summary: 'Não encontrado' })
    );
  });

  it('should show network error on status 0', () => {
    // Arrange & Act
    http.get('/api/test').subscribe({ error: () => {} });
    httpMock.expectOne('/api/test').error(new ProgressEvent('error'), { status: 0, statusText: '' });

    // Assert
    expect(messagesSpy.add).toHaveBeenCalledWith(
      expect.objectContaining({ severity: 'error', summary: 'Sem conexão' })
    );
  });

  it('should show conflict toast on 409', () => {
    // Arrange & Act
    http.put('/api/test', {}).subscribe({ error: () => {} });
    httpMock.expectOne('/api/test').flush({ detail: 'Registro já existe' }, { status: 409, statusText: 'Conflict' });

    // Assert
    expect(messagesSpy.add).toHaveBeenCalledWith(
      expect.objectContaining({ severity: 'warn', summary: 'Conflito' })
    );
  });
});
