import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, CreateUserRequest } from '../../shared/types/user';

const API_BASE = 'http://localhost:8080';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${API_BASE}/api/users`);
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${API_BASE}/api/users/${id}`);
  }

  createUser(user: CreateUserRequest): Observable<User> {
    return this.http.post<User>(`${API_BASE}/api/users`, user);
  }

  getPatients(): Observable<User[]> {
    return this.http.get<User[]>(`${API_BASE}/api/users/patients`);
  }

  getDoctors(): Observable<User[]> {
    return this.http.get<User[]>(`${API_BASE}/api/users/doctors`);
  }
}
