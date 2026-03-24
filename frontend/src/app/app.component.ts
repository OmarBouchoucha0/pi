import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TestcompComponent } from '../testcomp/testcomp.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TestcompComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'frontend';
}
