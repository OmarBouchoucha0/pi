import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, of, map } from 'rxjs';
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

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private userRoleSubject = new BehaviorSubject<string | null>(null);
  userRole$ = this.userRoleSubject.asObservable();

  private userSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.userSubject.asObservable();

  initialize(): Observable<boolean> {
    return this.getCurrentUser().pipe(
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
        this.storeUserData(response);
        this.userSubject.next({
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
        this.userRoleSubject.next(response.role);
      }),
    );
  }

  logout(): void {
    this.http
      .post(`${API_BASE}/api/users/auth/logout`, {})
      .pipe(catchError(() => of(null)))
      .subscribe(() => this.clearStorage());
  }

  refreshToken(): Observable<RefreshResponse> {
    const currentRole = this.userRoleSubject.getValue();
    return this.http.post<RefreshResponse>(`${API_BASE}/api/users/auth/refresh`, {}).pipe(
      tap((response) => {
        const user: User = {
          id: response.userId,
          email: '',
          firstName: '',
          lastName: '',
          phone: '',
          status: 'ACTIVE',
          createdAt: '',
          role: (currentRole as UserRole) || 'PATIENT',
          tenantId: 1,
        };
        this.userSubject.next(user);
        if (currentRole) {
          this.userRoleSubject.next(currentRole);
        }
      }),
    );
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${API_BASE}/api/users/auth/me`).pipe(
      tap((user) => {
        this.userSubject.next(user);
        this.userRoleSubject.next(user.role);
      }),
    );
  }

  register(credentials: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_BASE}/api/users/register/patient`, credentials).pipe(
      tap((response) => {
        this.storeUserData(response);
        this.userSubject.next({
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
        this.userRoleSubject.next(response.role);
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
          this.storeUserData(response);
          this.userSubject.next({
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
          this.userRoleSubject.next(response.role);
        }),
      );
  }

  getUser(): User | null {
    return this.userSubject.getValue();
  }

  getStoredUser(): User | null {
    return this.getUser();
  }

  getStoredRole(): string | null {
    return this.userRoleSubject.getValue();
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
    this.userSubject.next(updatedUser);
    return of(updatedUser);
  }

  getRole(): string | null {
    return this.userRoleSubject.getValue();
  }

  isAuthenticated(): boolean {
    return !!this.userSubject.getValue();
  }

  isAdmin(): boolean {
    return this.userRoleSubject.getValue() === 'ADMIN';
  }

  isDoctor(): boolean {
    return this.userRoleSubject.getValue() === 'DOCTOR';
  }

  isPatient(): boolean {
    return this.userRoleSubject.getValue() === 'PATIENT';
  }

  isLab(): boolean {
    return this.userRoleSubject.getValue() === 'LAB';
  }

  isNurse(): boolean {
    return this.userRoleSubject.getValue() === 'NURSE';
  }

  private storeUserData(response: AuthResponse): void {
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
    this.userSubject.next(user);
    this.userRoleSubject.next(response.role);
  }

  private clearStorage(): void {
    this.userSubject.next(null);
    this.userRoleSubject.next(null);
    this.router.navigate(['/login']);
  }
}
