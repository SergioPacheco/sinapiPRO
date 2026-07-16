import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { authGuard } from './auth.guard';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

describe('authGuard', () => {
  let authService: AuthService;
  let routerSpy: jest.Mocked<Router>;

  beforeEach(() => {
    localStorage.clear();
    routerSpy = { navigate: jest.fn() } as any;

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: Router, useValue: routerSpy },
      ],
    });

    authService = TestBed.inject(AuthService);
  });

  afterEach(() => localStorage.clear());

  const mockRoute = {} as ActivatedRouteSnapshot;
  const mockState = {} as RouterStateSnapshot;

  it('should allow access when user is authenticated', () => {
    // Arrange
    authService.login('valid-token', { id: '1', name: 'Test', email: 'test@test.com', roles: [] });

    // Act
    const result = TestBed.runInInjectionContext(() => authGuard(mockRoute, mockState));

    // Assert
    expect(result).toBe(true);
  });

  it('should deny access and redirect to /login when no token', () => {
    // Arrange — no login (default state)

    // Act
    const result = TestBed.runInInjectionContext(() => authGuard(mockRoute, mockState));

    // Assert
    expect(result).toBe(false);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should deny access after logout', () => {
    // Arrange
    authService.login('token', { id: '1', name: 'Test', email: 'test@test.com', roles: [] });
    authService.logout();

    // Act
    const result = TestBed.runInInjectionContext(() => authGuard(mockRoute, mockState));

    // Assert
    expect(result).toBe(false);
  });
});
