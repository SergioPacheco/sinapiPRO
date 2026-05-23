import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from './token.service';

/**
 * Guard that checks if the user has a required permission/role.
 * Usage in routes: canActivate: [permissionGuard('ROLE_ADMIN')]
 */
export function permissionGuard(requiredPermission: string): CanActivateFn {
  return () => {
    const tokenService = inject(TokenService);
    const router = inject(Router);

    const token = tokenService.getBearerToken();
    if (!token) {
      router.navigate(['/auth/login']);
      return false;
    }

    // Decode JWT payload to check permissions
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const scopes: string[] = payload.scope?.split(' ') || [];
      const roles: string[] = (payload.roles || []).map((r: string) => r.startsWith('ROLE_') ? r : `ROLE_${r}`);
      const allAuthorities = [...scopes.map(s => `SCOPE_${s}`), ...roles];

      if (allAuthorities.includes(requiredPermission)) {
        return true;
      }
    } catch {
      // Invalid token
    }

    router.navigate(['/403']);
    return false;
  };
}
