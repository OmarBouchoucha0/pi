import { Component } from '@angular/core';

@Component({
  selector: 'app-profile',
  standalone: true,
  template: `
    <div class="flex items-center justify-center h-full">
      <h1 class="text-[60px] font-bold tracking-tighter leading-none text-[#0a0a0a] select-none">
        Profile Settings
      </h1>
    </div>
  `,
})
export class ProfileComponent {}
