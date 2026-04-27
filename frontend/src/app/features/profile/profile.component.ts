import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../shared/types/user';
import { take } from 'rxjs';

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
    this.authService.currentUser$.pipe(take(1)).subscribe((user) => {
      this.user = user;
      if (user) {
        this.firstName = user.firstName || '';
        this.lastName = user.lastName || '';
        this.email = user.email || '';
        this.phone = user.phone || '';
      }
    });

    if (!this.user) {
      this.authService
        .getCurrentUser()
        .pipe(take(1))
        .subscribe((user) => {
          this.user = user;
          this.firstName = user.firstName || '';
          this.lastName = user.lastName || '';
          this.email = user.email || '';
          this.phone = user.phone || '';
        });
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
