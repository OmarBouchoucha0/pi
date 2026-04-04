import { Component } from '@angular/core';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { DrawerModule } from 'primeng/drawer';
import { ToolbarModule } from 'primeng/toolbar';
import { Tooltip } from 'primeng/tooltip';
import { HostListener } from '@angular/core';

@Component({
  selector: 'app-topbar',
  imports: [AvatarModule, ButtonModule, DrawerModule, ToolbarModule, Tooltip],
  templateUrl: './topbar.component.html',
  styleUrl: './topbar.component.scss',
})
export class TopbarComponent {
  notificationsOpen = true;
  settingsOpen = false;

  toggleNotifications(): void {
    this.notificationsOpen = !this.notificationsOpen;
  }

  toggleSettings(): void {
    this.settingsOpen = !this.settingsOpen;
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
