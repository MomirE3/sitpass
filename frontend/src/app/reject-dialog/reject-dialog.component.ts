import { Component, HostListener } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-reject-dialog',
  templateUrl: './reject-dialog.component.html',
  styleUrls: ['./reject-dialog.component.scss']
})
export class RejectDialogComponent {
  rejectionReason: string = '';

  constructor(public dialogRef: MatDialogRef<RejectDialogComponent>) {}

  @HostListener('document:keydown.escape', ['$event']) onKeydownHandler(event: KeyboardEvent) {
    this.onCancel();
  }

  onConfirm(): void {
    this.dialogRef.close(this.rejectionReason);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }
}
