import { Component, HostListener } from '@angular/core';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { DrawerModule } from 'primeng/drawer';
import { ToolbarModule } from 'primeng/toolbar';
import { Tooltip } from 'primeng/tooltip';

@Component({
  selector: 'app-admin-topbar',
  imports: [AvatarModule, ButtonModule, DrawerModule, ToolbarModule, Tooltip],
  templateUrl: './admin-topbar.component.html',
  styleUrl: './admin-topbar.component.scss',
})
export class AdminTopbarComponent {
  notificationsOpen = false;
  settingsOpen = false;

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
