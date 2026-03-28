import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { NotfoundComponent } from '../shared/components/notfound/notfound.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: 'signup',
    loadComponent: () =>
      import('./features/auth/signup/signup.component').then((m) => m.SignupComponent),
  },
  { path: '**', component: NotfoundComponent },
];
