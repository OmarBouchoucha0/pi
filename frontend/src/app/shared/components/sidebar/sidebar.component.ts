import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
})
export class SidebarComponent {
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
}
