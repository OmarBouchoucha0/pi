import { Component, HostListener, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { DrawerModule } from 'primeng/drawer';
import { ToolbarModule } from 'primeng/toolbar';
import { Tooltip } from 'primeng/tooltip';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../types/user';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-admin-topbar',
  imports: [CommonModule, AvatarModule, ButtonModule, DrawerModule, ToolbarModule, Tooltip],
  templateUrl: './admin-topbar.component.html',
  styleUrl: './admin-topbar.component.scss',
})
export class AdminTopbarComponent implements OnInit {
  private router = inject(Router);
  private authService = inject(AuthService);
  notificationsOpen = false;
  settingsOpen = false;
  user$!: Observable<User | null>;

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe();
    this.user$ = this.authService.currentUser$;
  }

  goToProfile(): void {
    this.router.navigate(['/admin/profile']);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    const clickedInsideDrawer = target.closest('.p-drawer');
    const clickedNotifBtn = target.closest('[data-drawer="notifications"]');
    const clickedSettingsBtn = target.closest('[data-drawer="settings"]');

    if (!clickedInsideDrawer && !clickedNotifBtn) {
      this.notificationsOpen = false;
    }
    if (!clickedInsideDrawer && !clickedSettingsBtn) {
      this.settingsOpen = false;
    }
  }
}
