import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AccountRequest } from './account-request.model';
import { RejectDialogComponent } from '../../reject-dialog/reject-dialog.component';
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { ApiResponse } from '../../api-response.model'; 
import { AuthService } from '../../services/auth.service'; 
import { Router } from '@angular/router';


@Component({
  selector: 'app-account-requests',
  templateUrl: './account-requests.component.html',
  styleUrls: ['./account-requests.component.scss']
})
export class AccountRequestsComponent implements OnInit {
  accountRequests: AccountRequest[] = [];

  constructor(
    private http: HttpClient,
    public dialog: MatDialog,
    private authService: AuthService,
    private router: Router 
  ) { }


  ngOnInit(): void {
    if (!this.authService.isLoggedIn() || !this.authService.isAdmin()) {
      this.authService.logout();
      this.router.navigate(['/login']);
      return;
    }
    this.getAccountRequests();
  }

  getAccountRequests(): void {
    this.http.get<AccountRequest[]>('http://localhost:8080/api/requests')
      .subscribe(requests => {
        this.accountRequests = requests;
      });
  }

  acceptRequest(id: number): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '400px';
    dialogConfig.autoFocus = true;
    dialogConfig.disableClose = false; 

    const dialogRef = this.dialog.open(ConfirmDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.http.patch<ApiResponse>(`http://localhost:8080/api/requests/acceptRequest/${id}`, {})
          .subscribe(response => {
            alert(response.message);
            this.getAccountRequests(); 
          }, error => {
            alert(error.error); 
          });
      }
    });
  }

  rejectRequest(id: number): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '400px';
    dialogConfig.autoFocus = true;
    dialogConfig.disableClose = false; 

    const dialogRef = this.dialog.open(RejectDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      if (result !== null) {
        this.http.patch<ApiResponse>(`http://localhost:8080/api/requests/rejectRequest/${id}`, { rejectionReason: result })
          .subscribe(response => {
            alert(response.message);
            this.getAccountRequests(); 
          }, error => {
            alert(error.error); 
          });
      }
    });
  }
}
