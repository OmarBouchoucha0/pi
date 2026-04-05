import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { NotfoundComponent } from './shared/components/notfound/notfound.component';
import { LayoutComponent } from './shared/components/layout/layout.component';
import { AdminLayoutComponent } from './shared/components/admin-layout/admin-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
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
    children: [
      { path: '', redirectTo: 'users', pathMatch: 'full' },
      {
        path: 'users',
        loadComponent: () =>
          import('./features/admin/users/users.component').then((m) => m.UsersComponent),
      },
    ],
  },
  { path: 'login', component: LoginComponent },
  {
    path: 'signup',
    loadComponent: () =>
      import('./features/auth/signup/signup.component').then((m) => m.SignupComponent),
  },
  { path: '**', component: NotfoundComponent },
];
