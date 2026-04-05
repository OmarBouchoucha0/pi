import { Component } from '@angular/core';
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
}
