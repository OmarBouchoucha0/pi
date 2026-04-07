import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { FloatLabelModule } from 'primeng/floatlabel';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { TiltDirective } from '../../../shared/directives/tilt.directive';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { MessageModule } from 'primeng/message';

@Component({
  selector: 'app-signup',
  imports: [
    ButtonModule,
    InputTextModule,
    PasswordModule,
    FormsModule,
    CommonModule,
    FloatLabelModule,
    TiltDirective,
    MessageModule,
  ],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss',
})
export class SignupComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  firstName = '';
  lastName = '';
  email = '';
  phone = '';
  password = '';
  confirmPassword = '';
  submitted = false;
  loading = false;
  error = '';

  get firstNameInvalid() {
    return !this.firstName.trim();
  }
  get lastNameInvalid() {
    return !this.lastName.trim();
  }
  get emailInvalid() {
    return !this.email.includes('@');
  }
  get phoneInvalid() {
    return !this.phone.trim();
  }
  get passwordInvalid() {
    return this.password.length < 6;
  }
  get confirmPasswordInvalid() {
    return this.password !== this.confirmPassword;
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = '';
    if (
      this.firstNameInvalid ||
      this.lastNameInvalid ||
      this.emailInvalid ||
      this.phoneInvalid ||
      this.passwordInvalid ||
      this.confirmPasswordInvalid
    )
      return;

    this.loading = true;
    this.authService
      .createPatient({
        email: this.email,
        password: this.password,
        firstName: this.firstName,
        lastName: this.lastName,
        phone: this.phone,
      })
      .subscribe({
        next: (response) => {
          this.loading = false;
          const redirectUrl = response.role === 'ADMIN' ? '/admin' : '/';
          this.router.navigate([redirectUrl]);
        },
        error: (err) => {
          this.loading = false;
          this.error = err.error?.message || 'Registration failed. Please try again.';
        },
      });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
