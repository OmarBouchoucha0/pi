import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Tooltip } from 'primeng/tooltip';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, Tooltip],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
})
export class SidebarComponent {
  private router = inject(Router);
  collapsed = false;

  menuItems = [
    { icon: 'home', label: 'Dashboard' },
    { icon: 'users', label: 'Patients' },
    { icon: 'calendar', label: 'Appointments' },
    { icon: 'chart', label: 'Analytics' },
    { icon: 'inbox', label: 'Messages' },
    { icon: 'settings', label: 'Settings' },
  ];

  toggle(): void {
    this.collapsed = !this.collapsed;
  }

  logout(): void {
    this.router.navigate(['/login']);
  }
}
