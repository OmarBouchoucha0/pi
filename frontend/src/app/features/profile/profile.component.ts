import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Avatar } from 'primeng/avatar';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, Avatar],
  templateUrl: './profile.component.html',
})
export class ProfileComponent {
  prefs = {
    notifications: true,
    twoFactor: false,
  };

  togglePref(key: keyof typeof this.prefs) {
    this.prefs[key] = !this.prefs[key];
  }
}
