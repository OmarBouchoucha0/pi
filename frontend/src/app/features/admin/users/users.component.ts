import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { AvatarModule } from 'primeng/avatar';
import { ChartModule } from 'primeng/chart';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { SelectModule } from 'primeng/select';
import { FloatLabelModule } from 'primeng/floatlabel';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { User, UserRole } from '../../../shared/types/user';

@Component({
  selector: 'app-admin-users',
  imports: [
    CommonModule,
    TableModule,
    AvatarModule,
    ChartModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    PasswordModule,
    SelectModule,
    FloatLabelModule,
    FormsModule,
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss',
})
export class UsersComponent implements OnInit {
  view: 'table' | 'chart' = 'table';
  users: User[] = [];
  loading = true;

  showAddUserDialog = false;
  submitted = false;
  loadingSubmit = false;
  errorMessage = '';

  firstName = '';
  lastName = '';
  phone = '';
  email = '';
  password = '';
  role: UserRole = 'PATIENT';

  roleOptions = [
    { label: 'Patient', value: 'PATIENT' },
    { label: 'Doctor', value: 'DOCTOR' },
    { label: 'Admin', value: 'ADMIN' },
    { label: 'Nurse', value: 'NURSE' },
    { label: 'Lab', value: 'LAB' },
  ];

  private userService = inject(UserService);

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  openAddUserDialog(): void {
    this.submitted = false;
    this.errorMessage = '';
    this.firstName = '';
    this.lastName = '';
    this.phone = '';
    this.email = '';
    this.password = '';
    this.role = 'PATIENT';
    this.showAddUserDialog = true;
  }

  closeAddUserDialog(): void {
    this.showAddUserDialog = false;
  }

  onSubmitUser(): void {
    this.submitted = true;
    this.errorMessage = '';

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
      .createUser({
        email: this.email,
        password: this.password,
        firstName: this.firstName,
        lastName: this.lastName,
        phone: this.phone,
        role: this.role,
      })
      .subscribe({
        next: () => {
          this.loadingSubmit = false;
          this.showAddUserDialog = false;
          this.loadUsers();
        },
        error: (error) => {
          this.loadingSubmit = false;
          this.errorMessage = error.error?.message || 'Failed to create user. Please try again.';
        },
      });
  }

  private validateEmail(email: string): boolean {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  }

  validateEmailCheck(email: string): boolean {
    return this.validateEmail(email);
  }

  getInitials(user: User): string {
    const first = user.firstName?.[0] || '';
    const last = user.lastName?.[0] || '';
    return (first + last).toUpperCase();
  }

  get stats() {
    return [
      { label: 'Total Users', value: this.users.length, sub: 'All time' },
      {
        label: 'Active',
        value: this.users.filter((u) => u.status === 'ACTIVE').length,
        sub: 'Currently active',
      },
      {
        label: 'Pending',
        value: this.users.filter((u) => u.status === 'INACTIVE').length,
        sub: 'Awaiting approval',
      },
      {
        label: 'Inactive',
        value: this.users.filter((u) => u.status === 'SUSPENDED').length,
        sub: 'Inactive accounts',
      },
    ];
  }

  get roleLabels(): string[] {
    const roles = [...new Set(this.users.map((u) => u.role))];
    return roles.length ? roles : ['Admin', 'Doctor', 'Patient'];
  }

  get roleChartData() {
    const roleCounts = this.users.reduce(
      (acc, user) => {
        acc[user.role] = (acc[user.role] || 0) + 1;
        return acc;
      },
      {} as Record<string, number>,
    );

    return {
      labels: Object.keys(roleCounts),
      datasets: [
        {
          data: Object.values(roleCounts),
          backgroundColor: ['#0a0a0a', '#6b7280', '#d1d5db', '#22c55e', '#f59e0b'],
          borderWidth: 0,
        },
      ],
    };
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
          backgroundColor: ['#22c55e', '#d1d5db', '#f59e0b', '#ef4444'],
          borderWidth: 0,
        },
      ],
    };
  }

  get joinedChartData() {
    const months = Array(12).fill(0);
    this.users.forEach((user) => {
      if (user.createdAt) {
        const month = new Date(user.createdAt).getMonth();
        if (month >= 0 && month < 12) {
          months[month]++;
        }
      }
    });

    return {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
      datasets: [
        {
          label: 'New Users',
          data: months,
          backgroundColor: '#0a0a0a',
          borderRadius: 4,
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
