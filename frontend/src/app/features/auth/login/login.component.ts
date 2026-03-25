import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { FloatLabelModule } from 'primeng/floatlabel';
import { FormsModule } from '@angular/forms';

import { Component, inject } from '@angular/core';
import { TiltDirective } from '../../../shared/directives/tilt.directive';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [
    ButtonModule,
    InputTextModule,
    PasswordModule,
    FormsModule,
    FloatLabelModule,
    TiltDirective,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  router = inject(Router);

  email = '';
  password = '';
  emailInvalid = false;
  passwordInvalid = false;
  submitted = false;

  onSubmit() {
    this.submitted = true;
    this.emailInvalid = !this.email || !this.validateEmail(this.email);
    this.passwordInvalid = !this.password || this.password.length < 6;
    if (!this.emailInvalid && !this.passwordInvalid) {
      console.log('Login submitted', { email: this.email, password: this.password });
      this.router.navigate(['/patient']);
    }
  }

  private validateEmail(email: string): boolean {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  }
}
