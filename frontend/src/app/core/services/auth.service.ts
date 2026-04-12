import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';
import {
  User,
  UserRole,
  LoginRequest,
  AuthResponse,
  RefreshResponse,
  RegisterRequest,
  CreatePatientRequest,
} from '../../shared/types/user';

const API_BASE = 'http://localhost:8080';
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

  initialize(): Observable<boolean> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return of(false);
    }
    return this.refreshToken().pipe(
      switchMap(() => this.getCurrentUser()),
      map(() => true),
      catchError((error) => {
        console.error('[Auth] Initialization failed:', error);
        this.clearStorage();
        return of(false);
      }),
    );
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_BASE}/api/users/auth/login`, credentials).pipe(
      tap((response) => {
        this.storeTokens(response);
        this.currentUserSubject.next({
          id: response.userId,
          email: response.email,
          firstName: '',
          lastName: '',
          phone: '',
          status: 'ACTIVE',
          createdAt: '',
          role: response.role,
          tenantId: 1,
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
    const currentRole = this.getRole() as UserRole | null;
    return this.http
      .post<RefreshResponse>(`${API_BASE}/api/users/auth/refresh`, {
        refreshToken,
      })
      .pipe(
        tap((response) => {
          localStorage.setItem(TOKEN_KEYS.ACCESS, response.accessToken);
          localStorage.setItem(TOKEN_KEYS.REFRESH, response.refreshToken);
          if (currentRole) {
            localStorage.setItem(TOKEN_KEYS.ROLE, currentRole);
          }
          const user: User = {
            id: response.userId,
            email: '',
            firstName: '',
            lastName: '',
            phone: '',
            status: 'ACTIVE',
            createdAt: '',
            role: currentRole || 'PATIENT',
            tenantId: 1,
          };
          localStorage.setItem(TOKEN_KEYS.USER, JSON.stringify(user));
        }),
      );
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${API_BASE}/api/users/auth/me`).pipe(
      tap((user) => {
        localStorage.setItem(TOKEN_KEYS.USER, JSON.stringify(user));
        localStorage.setItem(TOKEN_KEYS.ROLE, user.role);
        this.currentUserSubject.next(user);
      }),
    );
  }

  register(credentials: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_BASE}/api/users/register/patient`, credentials).pipe(
      tap((response) => {
        this.storeTokens(response);
        this.currentUserSubject.next({
          id: response.userId,
          email: response.email,
          firstName: '',
          lastName: '',
          phone: '',
          status: 'ACTIVE',
          createdAt: '',
          role: response.role,
          tenantId: 1,
        });
      }),
    );
  }

  createPatient(data: CreatePatientRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_BASE}/api/users/register/patient`, {
        ...data,
        tenantId: 1,
      })
      .pipe(
        tap((response) => {
          this.storeTokens(response);
          this.currentUserSubject.next({
            id: response.userId,
            email: response.email,
            firstName: '',
            lastName: '',
            phone: '',
            status: 'ACTIVE',
            createdAt: '',
            role: response.role,
            tenantId: 1,
          });
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

  getStoredUser(): User | null {
    return this.getUser();
  }

  updateProfile(data: {
    firstName?: string;
    lastName?: string;
    phone?: string;
  }): Observable<User | null> {
    const user = this.getUser();
    if (!user) {
      return of(null);
    }
    const updatedUser: User = {
      ...user,
      firstName: data.firstName ?? user.firstName,
      lastName: data.lastName ?? user.lastName,
      phone: data.phone ?? user.phone,
    };
    localStorage.setItem(TOKEN_KEYS.USER, JSON.stringify(updatedUser));
    this.currentUserSubject.next(updatedUser);
    return of(updatedUser);
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

  isLab(): boolean {
    return this.getRole() === 'LAB';
  }

  isNurse(): boolean {
    return this.getRole() === 'NURSE';
  }

  private storeTokens(response: AuthResponse): void {
    localStorage.setItem(TOKEN_KEYS.ACCESS, response.accessToken);
    localStorage.setItem(TOKEN_KEYS.REFRESH, response.refreshToken);
    localStorage.setItem(TOKEN_KEYS.ROLE, response.role);
    const user: User = {
      id: response.userId,
      email: response.email,
      firstName: '',
      lastName: '',
      phone: '',
      status: 'ACTIVE',
      createdAt: '',
      role: response.role,
      tenantId: 1,
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

  checkTokenExpiration(): boolean {
    const token = this.getAccessToken();
    if (!token) {
      return false;
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000;
      const now = Date.now();
      if (exp < now) {
        this.logout();
        return false;
      }
      return true;
    } catch {
      return false;
    }
  }
}
