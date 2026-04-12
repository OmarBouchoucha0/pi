import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../shared/types/user';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastModule],
  providers: [MessageService],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private messageService = inject(MessageService);

  user: User | null = null;
  saving = false;

  firstName = '';
  lastName = '';
  email = '';
  phone = '';

  ngOnInit(): void {
    this.user = this.authService.getStoredUser();
    if (this.user) {
      this.firstName = this.user.firstName || '';
      this.lastName = this.user.lastName || '';
      this.email = this.user.email || '';
      this.phone = this.user.phone || '';
    }
  }

  saveChanges(): void {
    if (!this.firstName.trim() || !this.lastName.trim()) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'First name and last name are required',
      });
      return;
    }

    this.saving = true;
    this.authService
      .updateProfile({
        firstName: this.firstName,
        lastName: this.lastName,
        phone: this.phone,
      })
      .subscribe({
        next: () => {
          this.saving = false;
          this.messageService.add({
            severity: 'success',
            summary: 'Saved',
            detail: 'Your profile has been updated',
          });
        },
        error: () => {
          this.saving = false;
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to update profile',
          });
        },
      });
  }
}
