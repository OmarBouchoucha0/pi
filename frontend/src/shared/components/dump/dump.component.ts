import { Component } from '@angular/core';
import { SidebarComponent } from '../../../app/shared/components/sidebar/sidebar.component';
import { TopbarComponent } from '../../../app/shared/components/topbar/topbar.component';

@Component({
  selector: 'app-dump',
  imports: [SidebarComponent, TopbarComponent],
  templateUrl: './dump.component.html',
  styleUrl: './dump.component.scss',
})
export class DumpComponent {}
