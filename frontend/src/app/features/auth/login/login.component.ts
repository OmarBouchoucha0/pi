import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { FloatLabelModule } from 'primeng/floatlabel';
import { FormsModule } from '@angular/forms';

import { Component, inject } from '@angular/core';
import { TiltDirective } from '../../../shared/directives/tilt.directive';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { MessageModule } from 'primeng/message';

@Component({
  selector: 'app-login',
  imports: [
    ButtonModule,
    InputTextModule,
    PasswordModule,
    FormsModule,
    FloatLabelModule,
    TiltDirective,
    MessageModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private authService = inject(AuthService);
  router = inject(Router);

  email = '';
  password = '';
  emailInvalid = false;
  passwordInvalid = false;
  submitted = false;
  loginError = '';
  loading = false;

  onSubmit() {
    this.submitted = true;
    this.loginError = '';
    this.emailInvalid = !this.email || !this.validateEmail(this.email);
    this.passwordInvalid = !this.password || this.password.length < 4;
    if (this.emailInvalid || this.passwordInvalid) {
      return;
    }

    this.loading = true;
    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (response) => {
        this.loading = false;
        const redirectUrl = response.role === 'ADMIN' ? '/admin' : '/';
        this.router.navigate([redirectUrl]);
      },
      error: (error) => {
        this.loading = false;
        this.loginError = error.error?.message || 'Login failed. Please check your credentials.';
      },
    });
  }

  private validateEmail(email: string): boolean {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  }
}
