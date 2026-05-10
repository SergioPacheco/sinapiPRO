import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, of } from 'rxjs';

import { Menu } from '@core';
import { Token, User } from './interface';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  protected readonly http = inject(HttpClient);

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
    return of<User>({
      name: 'SinapiPRO Admin',
      email: 'admin@sinapipro.dev',
      roles: ['ADMIN', 'USER'],
      permissions: ['sinapipro.read', 'sinapipro.write'],
    });
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
}

interface ApiTokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  accessTokenExpiresAt: string;
}
