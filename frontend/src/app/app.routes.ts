import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { NotfoundComponent } from './shared/components/notfound/notfound.component';
import { LayoutComponent } from './shared/components/layout/layout.component';
import { AdminLayoutComponent } from './shared/components/admin-layout/admin-layout.component';
import { authGuard, adminGuard, guestGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'placeholder', pathMatch: 'full' },
      {
        path: 'placeholder',
        loadComponent: () =>
          import('./features/placeholder/placeholder.component').then(
            (m) => m.PlaceholderComponent,
          ),
      },
      {
        path: 'ai-chat-bot',
        loadComponent: () =>
          import('./features/ai-chat-bot/ai-chat-bot.component').then((m) => m.AiChatBotComponent),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile.component').then((m) => m.ProfileComponent),
      },
    ],
  },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [adminGuard],
    children: [
      { path: '', redirectTo: 'users', pathMatch: 'full' },
      {
        path: 'users',
        loadComponent: () =>
          import('./features/admin/users/users.component').then((m) => m.UsersComponent),
      },
      {
        path: 'patients',
        loadComponent: () =>
          import('./features/admin/patients/patients.component').then((m) => m.PatientsComponent),
      },
      {
        path: 'doctors',
        loadComponent: () =>
          import('./features/admin/doctors/doctors.component').then((m) => m.DoctorsComponent),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile.component').then((m) => m.ProfileComponent),
      },
    ],
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    component: LoginComponent,
  },
  {
    path: 'signup',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/auth/signup/signup.component').then((m) => m.SignupComponent),
  },
  { path: '**', component: NotfoundComponent },
];
