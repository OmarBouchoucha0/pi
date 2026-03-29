import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { TopbarComponent } from '../topbar/topbar.component';

@Component({
  selector: 'app-dump',
  imports: [RouterOutlet, SidebarComponent, TopbarComponent],
  templateUrl: './dump.component.html',
  styleUrl: './dump.component.scss',
})
export class DumpComponent {}
