import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  isAdminOrManager = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.authService.isManagerOrAdmin().subscribe((isAdminOrManager) => {
      this.isAdminOrManager = isAdminOrManager;
    });
  }

  get isAdmin(): boolean {
    const userType = this.authService.getUserType();
    return userType === 'Administrator';
  }

  get isUser(): boolean {
    const userType = this.authService.getUserType();
    return userType === 'User';
  }

  logout(): void {
    this.authService.logout();
    localStorage.removeItem('token');
    localStorage.removeItem('token_expires');
    this.router.navigate(['/login']);
  }
}
