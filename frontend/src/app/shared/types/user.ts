export type UserRole = 'PATIENT' | 'DOCTOR' | 'ADMIN' | 'LAB' | 'NURSE';
export type UserStatus = 'ACTIVE' | 'LOCKED' | 'DISABLED';
export type TenantStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
export type Gender = 'MALE' | 'FEMALE' | 'OTHER';

export interface Tenant {
  id: number;
  name: string;
  status: TenantStatus;
  createdAt: string;
}

export interface Hospital {
  id: number;
  name: string;
  status: TenantStatus;
  tenant?: Tenant;
  tenantId?: number;
  createdAt?: string;
}

export interface Department {
  id: number;
  name: string;
  description?: string;
  tenant?: Tenant;
  tenantId?: number;
  hospital?: Hospital;
  createdAt?: string;
}

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  dateOfBirth?: string;
  gender?: Gender;
  status: UserStatus;
  createdAt: string;
  role: UserRole;

  medicalRecordNumber?: string;
  bloodType?: string;
  allergies?: string;
  chronicConditions?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;

  departmentId?: number;
  licenseNumber?: string;
  specialty?: string;

  privilegeLevel?: 'SUPER_ADMIN' | 'ADMIN' | 'STAFF';

  tenantId: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  userId: number;
  email: string;
  role: UserRole;
}

export interface RefreshResponse {
  accessToken: string;
  refreshToken: string;
  userId: number;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  tenantId: number;
}

export interface CreatePatientRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
}

export interface CreateUserRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: UserRole;
  tenantId: number;
  status?: UserStatus;
  // Patient-specific fields
  medicalRecordNumber?: string;
  bloodType?: string;
  allergies?: string;
  chronicConditions?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  // Doctor-specific fields
  licenseNumber?: string;
  specialty?: string;
  departmentId?: number;
}
