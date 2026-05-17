import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, of } from 'rxjs';

import { Menu } from '@core';
import { base64 } from './helpers';
import { Token, User } from './interface';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  protected readonly http = inject(HttpClient);
  private readonly tokenService = inject(TokenService);

  login(username: string, password: string, rememberMe = false) {
    return this.http
      .post<ApiTokenResponse>('/auth/token', { username, password, grantType: 'PASSWORD' })
      .pipe(map(token => this.toToken(token, true)));
  }

  refresh(params: Record<string, any>) {
    return this.http
      .post<ApiTokenResponse>('/auth/token', {
        refreshToken: params.refresh_token,
        grantType: 'REFRESH_TOKEN',
      })
      .pipe(map(token => this.toToken(token, true)));
  }

  logout() {
    return of({});
  }

  user() {
    return of(this.readUserFromToken());
  }

  menu() {
    return this.http.get<{ menu: Menu[] }>('data/menu.json').pipe(map(res => res.menu));
  }

  private toToken(token: ApiTokenResponse, includeRefreshToken: boolean): Token {
    const expiresAt = Date.parse(token.accessTokenExpiresAt);
    const expiresIn = Number.isNaN(expiresAt)
      ? undefined
      : Math.max(0, Math.floor((expiresAt - Date.now()) / 1000));

    return {
      access_token: token.accessToken,
      token_type: token.tokenType,
      expires_in: expiresIn,
      refresh_token: includeRefreshToken ? token.refreshToken : undefined,
    };
  }

  private readUserFromToken(): User {
    const accessToken = this.tokenService.getBearerToken().replace(/^Bearer\s+/i, '');
    if (!accessToken) {
      return {};
    }

    try {
      const [, payload] = accessToken.split('.');
      const claims = JSON.parse(base64.decode(payload)) as JwtClaims;
      const email = claims.sub;
      const permissions = typeof claims.scope === 'string'
        ? claims.scope.split(' ').filter(Boolean)
        : [];
      const roles = Array.isArray(claims.roles) ? claims.roles : [];

      return {
        email,
        name: this.deriveDisplayName(claims),
        roles,
        permissions,
      };
    } catch {
      return {};
    }
  }

  private deriveDisplayName(claims: JwtClaims): string {
    if (claims.name?.trim()) {
      return claims.name.trim();
    }

    const subject = claims.sub?.trim();
    if (!subject) {
      return 'Usuário';
    }

    const baseName = subject.includes('@') ? subject.split('@')[0] : subject;
    return baseName
      .split(/[._-]+/)
      .filter(Boolean)
      .map(part => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }
}

interface ApiTokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  accessTokenExpiresAt: string;
}

interface JwtClaims {
  sub?: string;
  name?: string;
  scope?: string;
  roles?: string[];
}
