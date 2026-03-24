import { Component } from '@angular/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

@Component({
  selector: 'app-testcomp',
  standalone: true,
  imports: [MatSlideToggleModule],
  templateUrl: './testcomp.component.html',
  styleUrl: './testcomp.component.scss',
})
export class TestcompComponent {

}
