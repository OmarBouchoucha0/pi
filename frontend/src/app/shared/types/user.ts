export type UserRole = 'PATIENT' | 'DOCTOR' | 'ADMIN' | 'LAB' | 'NURSE';
export type UserStatus = 'ACTIVE' | 'SUSPENDED' | 'INACTIVE';
export type Gender = 'MALE' | 'FEMALE' | 'OTHER';

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
}
