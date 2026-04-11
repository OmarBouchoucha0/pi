import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Tooltip } from 'primeng/tooltip';
import { inject } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-admin-sidebar',
  imports: [CommonModule, RouterModule, Tooltip],
  templateUrl: './admin-sidebar.component.html',
  styleUrl: './admin-sidebar.component.scss',
})
export class AdminSidebarComponent {
  private authService = inject(AuthService);

  collapsed = false;

  menuItems = [
    { icon: 'users', label: 'Users', route: '/admin/users' },
    { icon: 'patients', label: 'Patients', route: '/admin/patients' },
    { icon: 'doctors', label: 'Doctors', route: '/admin/doctors' },
  ];

  toggle(): void {
    this.collapsed = !this.collapsed;
  }

  logout(): void {
    this.authService.logout();
  }
}
