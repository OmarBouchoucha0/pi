import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { AvatarModule } from 'primeng/avatar';
import { ChartModule } from 'primeng/chart';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule, TableModule, AvatarModule, ChartModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class AdminDashboardComponent {
  view: 'table' | 'chart' = 'table';

  users = [
    {
      id: 1,
      name: 'Omar Bouchoucha',
      email: 'omar@example.com',
      initials: 'OB',
      role: 'Admin',
      status: 'Active',
      joined: '2024-01-10',
    },
    {
      id: 2,
      name: 'Sara Malik',
      email: 'sara@example.com',
      initials: 'SM',
      role: 'Editor',
      status: 'Active',
      joined: '2024-02-14',
    },
    {
      id: 3,
      name: 'James Carter',
      email: 'james@example.com',
      initials: 'JC',
      role: 'Viewer',
      status: 'Inactive',
      joined: '2024-03-05',
    },
    {
      id: 4,
      name: 'Lena Fischer',
      email: 'lena@example.com',
      initials: 'LF',
      role: 'Editor',
      status: 'Pending',
      joined: '2024-04-20',
    },
    {
      id: 5,
      name: 'Karim Nassar',
      email: 'karim@example.com',
      initials: 'KN',
      role: 'Viewer',
      status: 'Active',
      joined: '2024-04-22',
    },
    {
      id: 6,
      name: 'Aisha Diallo',
      email: 'aisha@example.com',
      initials: 'AD',
      role: 'Admin',
      status: 'Active',
      joined: '2024-05-01',
    },
    {
      id: 7,
      name: 'Tom Weber',
      email: 'tom@example.com',
      initials: 'TW',
      role: 'Viewer',
      status: 'Inactive',
      joined: '2024-06-15',
    },
  ];

  get stats() {
    return [
      { label: 'Total Users', value: this.users.length, sub: 'All time' },
      {
        label: 'Active',
        value: this.users.filter((u) => u.status === 'Active').length,
        sub: 'Currently active',
      },
      {
        label: 'Pending',
        value: this.users.filter((u) => u.status === 'Pending').length,
        sub: 'Awaiting approval',
      },
    ];
  }

  roleChartData = {
    labels: ['Admin', 'Editor', 'Viewer'],
    datasets: [
      {
        data: [2, 2, 3],
        backgroundColor: ['#0a0a0a', '#6b7280', '#d1d5db'],
        borderWidth: 0,
      },
    ],
  };

  statusChartData = {
    labels: ['Active', 'Inactive', 'Pending'],
    datasets: [
      {
        data: [4, 2, 1],
        backgroundColor: ['#22c55e', '#d1d5db', '#f59e0b'],
        borderWidth: 0,
      },
    ],
  };

  joinedChartData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        label: 'New Users',
        data: [1, 1, 1, 2, 1, 1],
        backgroundColor: '#0a0a0a',
        borderRadius: 4,
      },
    ],
  };

  doughnutOptions = {
    plugins: {
      legend: {
        position: 'bottom',
        labels: { font: { size: 11 }, padding: 16, boxWidth: 12 },
      },
    },
    cutout: '70%',
  };

  barOptions = {
    plugins: { legend: { display: false } },
    scales: {
      x: { grid: { display: false }, ticks: { font: { size: 11 } } },
      y: { grid: { color: 'rgba(0,0,0,0.05)' }, ticks: { font: { size: 11 }, stepSize: 1 } },
    },
  };
}
