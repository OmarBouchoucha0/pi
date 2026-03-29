import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { TiltDirective } from '../../directives/tilt.directive';

@Component({
  selector: 'app-notfound',
  imports: [ButtonModule, TiltDirective],
  templateUrl: './notfound.component.html',
  styleUrl: './notfound.component.scss',
})
export class NotfoundComponent {
  router = inject(Router);
}
