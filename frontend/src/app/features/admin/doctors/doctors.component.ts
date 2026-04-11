import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { AvatarModule } from 'primeng/avatar';
import { ChartModule } from 'primeng/chart';
import { UserService } from '../../../core/services/user.service';
import { User } from '../../../shared/types/user';

@Component({
  selector: 'app-doctors',
  standalone: true,
  imports: [CommonModule, TableModule, AvatarModule, ChartModule],
  templateUrl: './doctors.component.html',
  styleUrl: './doctors.component.scss',
})
export class DoctorsComponent implements OnInit {
  view: 'table' | 'chart' = 'table';
  users: User[] = [];
  loading = true;

  private userService = inject(UserService);

  ngOnInit(): void {
    this.loadUsers();
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
        value: this.users.filter((u) => u.status === 'INACTIVE').length,
        sub: 'Not active',
      },
      {
        label: 'Suspended',
        value: this.users.filter((u) => u.status === 'SUSPENDED').length,
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
