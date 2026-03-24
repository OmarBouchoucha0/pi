import { Component, ViewEncapsulation } from '@angular/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

@Component({
  selector: 'app-testcomp',
  standalone: true,
  imports: [MatSlideToggleModule],
  templateUrl: './testcomp.component.html',
  styleUrl: './testcomp.component.scss',
  encapsulation: ViewEncapsulation.None  // Test this
})
export class TestcompComponent {

}
