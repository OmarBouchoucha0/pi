import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, CreateUserRequest, Tenant, Hospital, Department } from '../../shared/types/user';

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

  updateUser(id: number, user: Partial<CreateUserRequest>): Observable<User> {
    return this.http.patch<User>(`${API_BASE}/api/users/${id}`, user);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE}/api/users/${id}`);
  }

  getPatients(): Observable<User[]> {
    return this.http.get<User[]>(`${API_BASE}/api/users/patients`);
  }

  getDoctors(): Observable<User[]> {
    return this.http.get<User[]>(`${API_BASE}/api/users/doctors`);
  }

  createPatient(user: CreateUserRequest): Observable<User> {
    return this.http.post<User>(`${API_BASE}/api/users/patients`, {
      ...user,
      tenantId: 1,
    });
  }

  createDoctor(user: CreateUserRequest): Observable<User> {
    return this.http.post<User>(`${API_BASE}/api/users/doctors`, {
      ...user,
      tenantId: 1,
    });
  }

  getTenants(): Observable<Tenant[]> {
    return this.http.get<Tenant[]>(`${API_BASE}/api/tenants`);
  }

  createTenant(name: string): Observable<Tenant> {
    return this.http.post<Tenant>(`${API_BASE}/api/tenants`, { name });
  }

  deleteTenant(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE}/api/tenants/${id}`);
  }

  updateTenant(id: number, data: { name?: string; status?: string }): Observable<Tenant> {
    return this.http.patch<Tenant>(`${API_BASE}/api/tenants/${id}`, data);
  }

  getDepartments(): Observable<Department[]> {
    return this.http.get<Department[]>(`${API_BASE}/api/departments`);
  }

  getDepartmentsByTenant(tenantId: number): Observable<Department[]> {
    return this.http.get<Department[]>(`${API_BASE}/api/departments/tenant/${tenantId}`);
  }

  createDepartment(data: {
    name: string;
    description?: string;
    tenant: { id: number };
    hospital: { id: number };
  }): Observable<Department> {
    return this.http.post<Department>(`${API_BASE}/api/departments`, data);
  }

  updateDepartment(
    id: number,
    data: { name?: string; description?: string; tenantId?: number },
  ): Observable<Department> {
    return this.http.patch<Department>(`${API_BASE}/api/departments/${id}`, data);
  }

  deleteDepartment(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE}/api/departments/${id}`);
  }

  getHospitals(): Observable<Hospital[]> {
    return this.http.get<Hospital[]>(`${API_BASE}/api/hospitals`);
  }

  getHospitalsByTenant(tenantId: number): Observable<Hospital[]> {
    return this.http.get<Hospital[]>(`${API_BASE}/api/hospitals/tenant/${tenantId}`);
  }

  createHospital(data: {
    name: string;
    tenant: { id: number };
    status?: string;
  }): Observable<Hospital> {
    return this.http.post<Hospital>(`${API_BASE}/api/hospitals`, data);
  }

  updateHospital(id: number, data: { name?: string; status?: string }): Observable<Hospital> {
    return this.http.patch<Hospital>(`${API_BASE}/api/hospitals/${id}`, data);
  }

  deleteHospital(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE}/api/hospitals/${id}`);
  }
}
