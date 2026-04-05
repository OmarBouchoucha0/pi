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
      joined: '2024-01-15',
    },
    {
      id: 3,
      name: 'James Carter',
      email: 'james@example.com',
      initials: 'JC',
      role: 'Viewer',
      status: 'Inactive',
      joined: '2024-02-03',
    },
    {
      id: 4,
      name: 'Lena Fischer',
      email: 'lena@example.com',
      initials: 'LF',
      role: 'Editor',
      status: 'Pending',
      joined: '2024-02-20',
    },
    {
      id: 5,
      name: 'Karim Nassar',
      email: 'karim@example.com',
      initials: 'KN',
      role: 'Viewer',
      status: 'Active',
      joined: '2024-03-05',
    },
    {
      id: 6,
      name: 'Aisha Diallo',
      email: 'aisha@example.com',
      initials: 'AD',
      role: 'Admin',
      status: 'Active',
      joined: '2024-03-12',
    },
    {
      id: 7,
      name: 'Tom Weber',
      email: 'tom@example.com',
      initials: 'TW',
      role: 'Viewer',
      status: 'Inactive',
      joined: '2024-03-18',
    },
    {
      id: 8,
      name: 'Maria Garcia',
      email: 'maria@example.com',
      initials: 'MG',
      role: 'Editor',
      status: 'Active',
      joined: '2024-04-01',
    },
    {
      id: 9,
      name: 'John Smith',
      email: 'john@example.com',
      initials: 'JS',
      role: 'Viewer',
      status: 'Active',
      joined: '2024-04-10',
    },
    {
      id: 10,
      name: 'Anna Schmidt',
      email: 'anna@example.com',
      initials: 'AS',
      role: 'Editor',
      status: 'Pending',
      joined: '2024-04-22',
    },
    {
      id: 11,
      name: 'David Lee',
      email: 'david@example.com',
      initials: 'DL',
      role: 'Viewer',
      status: 'Active',
      joined: '2024-05-05',
    },
    {
      id: 12,
      name: 'Sophie Brown',
      email: 'sophie@example.com',
      initials: 'SB',
      role: 'Viewer',
      status: 'Inactive',
      joined: '2024-05-14',
    },
    {
      id: 13,
      name: 'Michael Chen',
      email: 'michael@example.com',
      initials: 'MC',
      role: 'Admin',
      status: 'Active',
      joined: '2024-05-20',
    },
    {
      id: 14,
      name: 'Emma Wilson',
      email: 'emma@example.com',
      initials: 'EW',
      role: 'Editor',
      status: 'Active',
      joined: '2024-06-01',
    },
    {
      id: 15,
      name: 'Robert Taylor',
      email: 'robert@example.com',
      initials: 'RT',
      role: 'Viewer',
      status: 'Pending',
      joined: '2024-06-10',
    },
    {
      id: 16,
      name: 'Lisa Anderson',
      email: 'lisa@example.com',
      initials: 'LA',
      role: 'Viewer',
      status: 'Active',
      joined: '2024-06-15',
    },
    {
      id: 17,
      name: 'Chris Martin',
      email: 'chris@example.com',
      initials: 'CM',
      role: 'Editor',
      status: 'Inactive',
      joined: '2024-07-02',
    },
    {
      id: 18,
      name: 'Rachel Green',
      email: 'rachel@example.com',
      initials: 'RG',
      role: 'Viewer',
      status: 'Active',
      joined: '2024-07-12',
    },
    {
      id: 19,
      name: 'Daniel White',
      email: 'daniel@example.com',
      initials: 'DW',
      role: 'Editor',
      status: 'Active',
      joined: '2024-07-20',
    },
    {
      id: 20,
      name: 'Jennifer Davis',
      email: 'jennifer@example.com',
      initials: 'JD',
      role: 'Viewer',
      status: 'Pending',
      joined: '2024-08-01',
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
      {
        label: 'Inactive',
        value: this.users.filter((u) => u.status === 'Inactive').length,
        sub: 'Inactive accounts',
      },
    ];
  }

  roleChartData = {
    labels: ['Admin', 'Editor', 'Viewer'],
    datasets: [
      { data: [3, 7, 10], backgroundColor: ['#0a0a0a', '#6b7280', '#d1d5db'], borderWidth: 0 },
    ],
  };

  statusChartData = {
    labels: ['Active', 'Inactive', 'Pending'],
    datasets: [
      { data: [12, 5, 3], backgroundColor: ['#22c55e', '#d1d5db', '#f59e0b'], borderWidth: 0 },
    ],
  };

  joinedChartData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug'],
    datasets: [
      {
        label: 'New Users',
        data: [2, 2, 4, 3, 3, 4, 3, 1],
        backgroundColor: '#0a0a0a',
        borderRadius: 4,
      },
    ],
  };

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
      y: { grid: { color: 'rgba(0,0,0,0.05)' }, ticks: { font: { size: 11 }, stepSize: 1 } },
    },
  };
}
