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
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule, AvatarModule, ButtonModule, DrawerModule, ToolbarModule, Tooltip],
  templateUrl: './topbar.component.html',
  styleUrl: './topbar.component.scss',
})
export class TopbarComponent implements OnInit {
  notificationsOpen = false;
  settingsOpen = false;
  user$!: Observable<User | null>;
  private router = inject(Router);
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe();
    this.user$ = this.authService.currentUser$;
  }

  toggleNotifications(): void {
    this.notificationsOpen = true;
  }

  toggleSettings(): void {
    this.settingsOpen = true;
  }

  goToProfile(): void {
    this.router.navigate(['/profile']);
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
