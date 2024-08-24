import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegistrationService } from '../services/registration.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent {
  email: string = '';
  address: string = '';

  constructor(private accountRequestService: RegistrationService, private router: Router) {}

  register(): void {
    if (!this.email.trim() || !this.address.trim()) {
      alert('You must fill all fields.');
      return;
    }

    this.accountRequestService.createRequest(this.email, this.address).subscribe({
      next: (resp) => {
        alert(resp.message);
        this.router.navigate(['/login']);
      },
      error: (error) => {
        alert(error.message);
      }
    });
  }
}
