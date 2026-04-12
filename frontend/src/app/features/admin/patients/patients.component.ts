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
import { FloatLabelModule } from 'primeng/floatlabel';
import { SelectModule } from 'primeng/select';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { User, UserRole, UserStatus, CreateUserRequest } from '../../../shared/types/user';

@Component({
  selector: 'app-patients',
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
    FloatLabelModule,
    SelectModule,
    FormsModule,
  ],
  providers: [ConfirmationService, MessageService],
  templateUrl: './patients.component.html',
  styleUrl: './patients.component.scss',
})
export class PatientsComponent implements OnInit {
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
  medicalRecordNumber = '';
  bloodType = '';
  allergies = '';
  role: UserRole = 'PATIENT';
  status: UserStatus = 'ACTIVE';

  statusOptions: { label: string; value: string }[] = [
    { label: 'Active', value: 'ACTIVE' },
    { label: 'Locked', value: 'LOCKED' },
    { label: 'Disabled', value: 'DISABLED' },
  ];

  private userService = inject(UserService);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getPatients().subscribe({
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
    this.medicalRecordNumber = '';
    this.bloodType = '';
    this.allergies = '';
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
            detail: 'Patient has been updated',
          });
          this.loadUsers();
        },
        error: (error) => {
          this.loadingSubmit = false;
          this.errorMessage = error.error?.message || 'Failed to update patient. Please try again.';
        },
      });
    } else {
      const emailInvalid = !this.email || !this.validateEmail(this.email);
      const passwordInvalid = !this.password || this.password.length < 6;
      const firstNameInvalid = !this.firstName.trim();
      const lastNameInvalid = !this.lastName.trim();
      const phoneInvalid = !this.phone.trim();

      if (emailInvalid || passwordInvalid || firstNameInvalid || lastNameInvalid || phoneInvalid) {
        return;
      }

      this.loadingSubmit = true;

      this.userService
        .createPatient({
          email: this.email,
          password: this.password,
          firstName: this.firstName,
          lastName: this.lastName,
          phone: this.phone,
          role: 'PATIENT',
          tenantId: 1,
          medicalRecordNumber: this.medicalRecordNumber || undefined,
          bloodType: this.bloodType || undefined,
          allergies: this.allergies || undefined,
        })
        .subscribe({
          next: () => {
            this.loadingSubmit = false;
            this.showAddDialog = false;
            this.messageService.add({
              severity: 'success',
              summary: 'Created',
              detail: 'Patient has been created',
            });
            this.loadUsers();
          },
          error: (error) => {
            this.loadingSubmit = false;
            this.errorMessage =
              error.error?.message || 'Failed to create patient. Please try again.';
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
      header: 'Delete Patient',
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
      { label: 'Total Patients', value: this.users.length, sub: 'All time' },
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

  get genderChartData() {
    const genderCounts = this.users.reduce(
      (acc, user) => {
        const gender = user.gender || 'OTHER';
        acc[gender] = (acc[gender] || 0) + 1;
        return acc;
      },
      {} as Record<string, number>,
    );

    return {
      labels: Object.keys(genderCounts),
      datasets: [
        {
          data: Object.values(genderCounts),
          backgroundColor: ['#0a0a0a', '#6b7280', '#d1d5db'],
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
