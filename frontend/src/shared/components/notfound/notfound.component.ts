import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { TopbarComponent } from '../topbar/topbar.component';
import { TiltDirective } from '../../../app/shared/directives/tilt.directive';

@Component({
  selector: 'app-notfound',
  imports: [ButtonModule, TopbarComponent, TiltDirective],
  templateUrl: './notfound.component.html',
  styleUrl: './notfound.component.scss',
})
export class NotfoundComponent {
  router = inject(Router);
}
