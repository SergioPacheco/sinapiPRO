import { Injectable, signal, computed } from '@angular/core';
import { Router } from '@angular/router';

export interface User {
  id: string;
  name: string;
  email: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'sp_token';
  private readonly USER_KEY = 'sp_user';

  user = signal<User | null>(this.loadUser());
  isAuthenticated = computed(() => !!this.user() && !!this.getToken());

  constructor(private router: Router) {}

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  login(token: string, user: User) {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.user.set(user);
  }

  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.user.set(null);
    this.router.navigate(['/login']);
  }

  private loadUser(): User | null {
    const raw = localStorage.getItem(this.USER_KEY);
    return raw ? JSON.parse(raw) : null;
  }
}
