import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from './service/auth.service';
import { User } from './models/auth.models';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
    template: `
    <div class="app-container">
      <router-outlet></router-outlet>
    </div>
  `,
   styles: [
    `
    .app-container {
      min-height: 100vh;
      background-color: #f5f5f5;
    }
  `,
  ],
})
export class AppComponent {
  title = "Gerenciador de Cartões"
  currentUser: User | null = null

  constructor(
    public authService: AuthService,
    private router: Router,
  ) {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user
    })
  }

  logout(): void {
    this.authService.logout()
    this.router.navigate(["/login"])
  }

  isLoginPage(): boolean {
    return this.router.url === "/login"
  }
}
