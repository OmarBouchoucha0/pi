import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Tooltip } from 'primeng/tooltip';
import { inject } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterModule, Tooltip],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
})
export class SidebarComponent {
  private authService = inject(AuthService);

  collapsed = false;

  menuItems = [
    { icon: 'robot', label: 'Chat Bot', route: '/ai-chat-bot' },
    { icon: 'calendar', label: 'Appointments', route: '/placeholder' },
  ];

  toggle(): void {
    this.collapsed = !this.collapsed;
  }

  logout(): void {
    this.authService.logout();
  }
}
