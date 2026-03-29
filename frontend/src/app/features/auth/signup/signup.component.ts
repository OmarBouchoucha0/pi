import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { FloatLabelModule } from 'primeng/floatlabel';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { TiltDirective } from '../../../shared/directives/tilt.directive';
import { Router } from '@angular/router';

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
  ],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss',
})
export class SignupComponent {
  router = inject(Router);

  firstName = '';
  lastName = '';
  email = '';
  password = '';
  confirmPassword = '';
  submitted = false;

  get firstNameInvalid() {
    return !this.firstName.trim();
  }
  get lastNameInvalid() {
    return !this.lastName.trim();
  }
  get emailInvalid() {
    return !this.email.includes('@');
  }
  get passwordInvalid() {
    return this.password.length < 6;
  }
  get confirmPasswordInvalid() {
    return this.password !== this.confirmPassword;
  }

  onSubmit(): void {
    this.submitted = true;
    if (
      this.firstNameInvalid ||
      this.lastNameInvalid ||
      this.emailInvalid ||
      this.passwordInvalid ||
      this.confirmPasswordInvalid
    )
      return;
    // your register logic here
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
