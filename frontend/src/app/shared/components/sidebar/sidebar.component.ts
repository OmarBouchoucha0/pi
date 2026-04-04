import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Tooltip } from 'primeng/tooltip';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterModule, Tooltip],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
})
export class SidebarComponent {
  collapsed = false;

  menuItems = [
    { icon: 'home', label: 'Dashboard', route: '/placeholder' },
    { icon: 'users', label: 'Patients', route: '/placeholder' },
    { icon: 'calendar', label: 'Appointments', route: '/placeholder' },
    { icon: 'chart', label: 'Analytics', route: '/placeholder' },
    { icon: 'inbox', label: 'Messages', route: '/placeholder' },
    { icon: 'settings', label: 'Settings', route: '/placeholder' },
  ];

  toggle(): void {
    this.collapsed = !this.collapsed;
  }
}
