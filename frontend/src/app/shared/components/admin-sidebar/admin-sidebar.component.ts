import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Tooltip } from 'primeng/tooltip';

@Component({
  selector: 'app-admin-sidebar',
  imports: [CommonModule, RouterModule, Tooltip],
  templateUrl: './admin-sidebar.component.html',
  styleUrl: './admin-sidebar.component.scss',
})
export class AdminSidebarComponent {
  collapsed = false;

  menuItems = [
    { icon: 'home', label: 'Dashboard', route: '/admin/dashboard' },
    { icon: 'users', label: 'Users', route: '/admin/users' },
  ];

  toggle(): void {
    this.collapsed = !this.collapsed;
  }
}
