import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { AvatarModule } from 'primeng/avatar';
import { ChartModule } from 'primeng/chart';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { ConfirmationService, MessageService } from 'primeng/api';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { SelectModule } from 'primeng/select';
import { FloatLabelModule } from 'primeng/floatlabel';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import {
  User,
  UserRole,
  UserStatus,
  CreateUserRequest,
  Department,
} from '../../../shared/types/user';

@Component({
  selector: 'app-doctors',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    AvatarModule,
    ChartModule,
    DialogModule,
    ConfirmDialogModule,
    ToastModule,
    ButtonModule,
    InputTextModule,
    PasswordModule,
    SelectModule,
    FloatLabelModule,
    FormsModule,
  ],
  providers: [ConfirmationService, MessageService],
  templateUrl: './doctors.component.html',
  styleUrl: './doctors.component.scss',
})
export class DoctorsComponent implements OnInit {
  view: 'table' | 'chart' = 'table';
  users: User[] = [];
  loading = true;

  showEditDialog = false;
  showAddDialog = false;
  submitted = false;
  loadingSubmit = false;
  errorMessage = '';

  isEditMode = false;
  selectedUser: User | null = null;
  selectedUserId: number | null = null;

  firstName = '';
  lastName = '';
  phone = '';
  email = '';
  password = '';
  specialty = '';
  licenseNumber = '';
  role: UserRole = 'DOCTOR';
  status: UserStatus = 'ACTIVE';

  statusOptions: { label: string; value: string }[] = [
    { label: 'Active', value: 'ACTIVE' },
    { label: 'Locked', value: 'LOCKED' },
    { label: 'Disabled', value: 'DISABLED' },
  ];

  departments: Department[] = [];
  selectedDepartmentId: number | null = null;

  private userService = inject(UserService);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);

  ngOnInit(): void {
    this.loadDepartments();
    this.loadUsers();
  }

  loadDepartments(): void {
    this.userService.getDepartments().subscribe({
      next: (departments) => {
        this.departments = departments;
      },
    });
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getDoctors().subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  openEditDialog(user: User): void {
    this.submitted = false;
    this.errorMessage = '';
    this.isEditMode = true;
    this.selectedUser = user;
    this.selectedUserId = user.id ?? null;
    this.firstName = user.firstName ?? '';
    this.lastName = user.lastName ?? '';
    this.phone = user.phone ?? '';
    this.email = user.email ?? '';
    this.specialty = user.specialty ?? '';
    this.status = user.status ?? 'ACTIVE';
    this.showEditDialog = true;
  }

  closeEditDialog(): void {
    this.showEditDialog = false;
    this.isEditMode = false;
    this.selectedUser = null;
    this.selectedUserId = null;
  }

  openAddDialog(): void {
    this.submitted = false;
    this.errorMessage = '';
    this.firstName = '';
    this.lastName = '';
    this.phone = '';
    this.email = '';
    this.password = '';
    this.specialty = '';
    this.licenseNumber = '';
    this.selectedDepartmentId = this.departments.length > 0 ? this.departments[0].id : null;
    this.showAddDialog = true;
  }

  closeAddDialog(): void {
    this.showAddDialog = false;
  }

  onSubmitUser(): void {
    this.submitted = true;
    this.errorMessage = '';

    if (this.isEditMode) {
      const firstNameInvalid = !this.firstName.trim();
      const lastNameInvalid = !this.lastName.trim();
      const phoneInvalid = !this.phone.trim();

      if (firstNameInvalid || lastNameInvalid || phoneInvalid) {
        return;
      }

      this.loadingSubmit = true;

      const updateData: Partial<CreateUserRequest> = {
        firstName: this.firstName,
        lastName: this.lastName,
        phone: this.phone,
        status: this.status,
      };

      this.userService.updateUser(this.selectedUserId!, updateData).subscribe({
        next: () => {
          this.loadingSubmit = false;
          this.showEditDialog = false;
          this.messageService.add({
            severity: 'success',
            summary: 'Updated',
            detail: 'Doctor has been updated',
          });
          this.loadUsers();
        },
        error: (error) => {
          this.loadingSubmit = false;
          this.errorMessage = error.error?.message || 'Failed to update doctor. Please try again.';
        },
      });
    } else {
      const emailInvalid = !this.email || !this.validateEmail(this.email);
      const passwordInvalid = !this.password || this.password.length < 6;
      const firstNameInvalid = !this.firstName.trim();
      const lastNameInvalid = !this.lastName.trim();
      const phoneInvalid = !this.phone.trim();
      const licenseNumberInvalid = !this.licenseNumber.trim();
      const departmentInvalid = !this.selectedDepartmentId;

      if (
        emailInvalid ||
        passwordInvalid ||
        firstNameInvalid ||
        lastNameInvalid ||
        phoneInvalid ||
        licenseNumberInvalid ||
        departmentInvalid
      ) {
        return;
      }

      this.loadingSubmit = true;

      this.userService
        .createDoctor({
          email: this.email,
          password: this.password,
          firstName: this.firstName,
          lastName: this.lastName,
          phone: this.phone,
          role: 'DOCTOR',
          tenantId: 1,
          licenseNumber: this.licenseNumber,
          specialty: this.specialty || undefined,
          departmentId: this.selectedDepartmentId || 1,
        })
        .subscribe({
          next: () => {
            this.loadingSubmit = false;
            this.showAddDialog = false;
            this.messageService.add({
              severity: 'success',
              summary: 'Created',
              detail: 'Doctor has been created',
            });
            this.loadUsers();
          },
          error: (error) => {
            this.loadingSubmit = false;
            this.errorMessage =
              error.error?.message || 'Failed to create doctor. Please try again.';
          },
        });
    }
  }

  validateEmail(email: string): boolean {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  }

  openDeleteDialog(user: User, event: Event): void {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: `Are you sure you want to delete ${user.firstName} ${user.lastName}?`,
      header: 'Delete Doctor',
      icon: 'pi pi-exclamation-triangle',
      rejectLabel: 'Cancel',
      rejectButtonProps: {
        label: 'Cancel',
        severity: 'secondary',
        outlined: true,
      },
      acceptButtonProps: {
        label: 'Delete',
        severity: 'danger',
      },
      accept: () => {
        this.loading = true;
        this.userService.deleteUser(user.id).subscribe({
          next: () => {
            this.loading = false;
            this.messageService.add({
              severity: 'success',
              summary: 'Deleted',
              detail: `${user.firstName} ${user.lastName} has been deleted`,
            });
            this.loadUsers();
          },
          error: () => {
            this.loading = false;
          },
        });
      },
    });
  }

  getInitials(user: User): string {
    const first = user.firstName?.[0] || '';
    const last = user.lastName?.[0] || '';
    return (first + last).toUpperCase();
  }

  get stats() {
    return [
      { label: 'Total Doctors', value: this.users.length, sub: 'All time' },
      {
        label: 'Active',
        value: this.users.filter((u) => u.status === 'ACTIVE').length,
        sub: 'Currently active',
      },
      {
        label: 'Inactive',
        value: this.users.filter((u) => u.status === 'DISABLED').length,
        sub: 'Not active',
      },
      {
        label: 'Suspended',
        value: this.users.filter((u) => u.status === 'LOCKED').length,
        sub: 'Suspended accounts',
      },
    ];
  }

  get statusChartData() {
    const statusCounts = this.users.reduce(
      (acc, user) => {
        acc[user.status] = (acc[user.status] || 0) + 1;
        return acc;
      },
      {} as Record<string, number>,
    );

    return {
      labels: Object.keys(statusCounts),
      datasets: [
        {
          data: Object.values(statusCounts),
          backgroundColor: ['#22c55e', '#d1d5db', '#ef4444', '#f59e0b'],
          borderWidth: 0,
        },
      ],
    };
  }

  get specialtyChartData() {
    const specialtyCounts = this.users.reduce(
      (acc, user) => {
        const specialty = user.specialty || 'Other';
        acc[specialty] = (acc[specialty] || 0) + 1;
        return acc;
      },
      {} as Record<string, number>,
    );

    return {
      labels: Object.keys(specialtyCounts),
      datasets: [
        {
          data: Object.values(specialtyCounts),
          backgroundColor: ['#0a0a0a', '#6b7280', '#d1d5db', '#22c55e', '#f59e0b'],
          borderWidth: 0,
        },
      ],
    };
  }

  doughnutOptions = {
    plugins: {
      legend: { position: 'bottom', labels: { font: { size: 11 }, padding: 16, boxWidth: 12 } },
    },
    cutout: '70%',
  };

  barOptions = {
    plugins: { legend: { display: false } },
    scales: {
      x: { grid: { display: false }, ticks: { font: { size: 11 } } },
      y: { grid: { color: 'rgba(0,0,0,0.05)' }, ticks: { font: { size: 11 }, stepSize: 5 } },
    },
  };
}
