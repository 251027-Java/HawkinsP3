import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="angular-mfe">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    .angular-mfe {
      font-family: var(--font-family, 'Inter', -apple-system, BlinkMacSystemFont, sans-serif);
      min-height: 100vh;
      background: var(--color-background, #f8fafc);
    }
  `]
})
export class App { }
