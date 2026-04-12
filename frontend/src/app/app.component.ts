import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { interval, Subscription } from 'rxjs';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'frontend';
  private authService = inject(AuthService);
  private tokenCheckSubscription?: Subscription;

  ngOnInit(): void {
    this.tokenCheckSubscription = interval(60000).subscribe(() => {
      this.authService.checkTokenExpiration();
    });
  }

  ngOnDestroy(): void {
    this.tokenCheckSubscription?.unsubscribe();
  }
}
