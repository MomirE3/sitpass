import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  email: string = '';
  password: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    if (!this.email.trim() || !this.password.trim()) {
      alert('You must fill all fields.');
      return;
    }

    this.authService.login(this.email, this.password).subscribe({
      next: (resp) => {
        localStorage.setItem('token', resp.accessToken);
        localStorage.setItem('token_expires', (Date.now() + resp.expiresIn).toString());
        alert('You have logged in successfully!');
        this.router.navigate(['/facilities']);
      },
      error: (error) => {
        alert('Bad credentials. ');
      }
    });
  }
}
