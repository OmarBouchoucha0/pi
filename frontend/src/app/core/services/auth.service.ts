import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, of } from 'rxjs';
import { Router } from '@angular/router';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  userId: number;
  email: string;
  role: string;
}

export interface RefreshResponse {
  accessToken: string;
  refreshToken: string;
  userId: number;
}

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  [key: string]: unknown;
}

const API_BASE = 'http://localhost:8082/pi';
const TOKEN_KEYS = {
  ACCESS: 'accessToken',
  REFRESH: 'refreshToken',
  USER: 'currentUser',
  ROLE: 'userRole',
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private currentUserSubject = new BehaviorSubject<User | null>(this.getUser());
  currentUser$ = this.currentUserSubject.asObservable();

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_BASE}/api/users/auth/login`, credentials).pipe(
      tap((response) => {
        this.storeTokens(response);
        this.currentUserSubject.next({
          id: response.userId,
          email: response.email,
          firstName: '',
          lastName: '',
          role: response.role,
        });
      }),
    );
  }

  logout(): void {
    const refreshToken = this.getRefreshToken();
    if (refreshToken) {
      this.http
        .post(`${API_BASE}/api/users/auth/logout`, { refreshToken })
        .pipe(catchError(() => of(null)))
        .subscribe(() => this.clearStorage());
    } else {
      this.clearStorage();
    }
  }

  refreshToken(): Observable<RefreshResponse> {
    const refreshToken = this.getRefreshToken();
    return this.http
      .post<RefreshResponse>(`${API_BASE}/api/users/auth/refresh`, {
        refreshToken,
      })
      .pipe(
        tap((response) => {
          this.storeTokens({
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            userId: response.userId,
            email: '',
            role: '',
          });
        }),
      );
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${API_BASE}/api/users/auth/me`).pipe(
      tap((user) => {
        localStorage.setItem(TOKEN_KEYS.USER, JSON.stringify(user));
        this.currentUserSubject.next(user);
      }),
    );
  }

  getAccessToken(): string | null {
    return localStorage.getItem(TOKEN_KEYS.ACCESS);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(TOKEN_KEYS.REFRESH);
  }

  getUser(): User | null {
    const userStr = localStorage.getItem(TOKEN_KEYS.USER);
    return userStr ? JSON.parse(userStr) : null;
  }

  getRole(): string | null {
    return localStorage.getItem(TOKEN_KEYS.ROLE);
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  isDoctor(): boolean {
    return this.getRole() === 'DOCTOR';
  }

  isPatient(): boolean {
    return this.getRole() === 'PATIENT';
  }

  private storeTokens(response: LoginResponse): void {
    localStorage.setItem(TOKEN_KEYS.ACCESS, response.accessToken);
    localStorage.setItem(TOKEN_KEYS.REFRESH, response.refreshToken);
    localStorage.setItem(TOKEN_KEYS.ROLE, response.role);
    const user: User = {
      id: response.userId,
      email: response.email,
      firstName: '',
      lastName: '',
      role: response.role,
    };
    localStorage.setItem(TOKEN_KEYS.USER, JSON.stringify(user));
  }

  private clearStorage(): void {
    localStorage.removeItem(TOKEN_KEYS.ACCESS);
    localStorage.removeItem(TOKEN_KEYS.REFRESH);
    localStorage.removeItem(TOKEN_KEYS.USER);
    localStorage.removeItem(TOKEN_KEYS.ROLE);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }
}
