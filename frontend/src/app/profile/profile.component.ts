import { Component, OnInit } from '@angular/core';
import { ProfileService } from '../services/profile.service';
import { AuthService } from '../services/auth.service';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Profile, VisitHistory } from './profile.model';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { AlertDialogComponent } from '../alert-dialog/alert-dialog.component';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  profileForm: FormGroup;
  profile: Profile = {
    email: '',
    name: '',
    surname: '',
    phoneNumber: '',
    birthday: new Date(),
    address: '',
    city: '',
    zipCode: '',
    imageId: undefined,
    imagePath: '',
    reviews: [],
    managedFacilities: [],
    visitHistory: []
  };
  profilePictureUrl: string | ArrayBuffer | null = null;
  showOldPassword: boolean = false;
  showNewPassword: boolean = false;
  showConfirmNewPassword: boolean = false;
  selectedFile: File | null = null;
  isAdmin: boolean = false;

  constructor(
    private profileService: ProfileService,
    private authService: AuthService,
    private fb: FormBuilder,
    private dialog: MatDialog
  ) {
    this.profileForm = this.fb.group({
      name: [''],
      surname: [''],
      phoneNumber: [''],
      birthday: [''],
      address: [''],
      city: [''],
      zipCode: [''],
      oldPassword: [''],
      newPassword: ['', Validators.minLength(6)],
      confirmNewPassword: ['']
    }, { validator: this.passwordsMatchValidator });
  }

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();

    this.profileService.getProfile().subscribe((profile: Profile) => {
      profile.visitHistory = profile.visitHistory?.map((visit: VisitHistory) => {
        return {
          ...visit,
          fromDateTime: this.convertToDate(visit.fromDateTime),
          toDateTime: this.convertToDate(visit.toDateTime)
        };
      }) || [];

      profile.birthday = this.convertToDate(profile.birthday);

      this.profile = profile;
      const birthdayString = profile.birthday ? profile.birthday.toISOString().split('T')[0] : '';
      this.profileForm.patchValue({
        name: profile.name,
        surname: profile.surname,
        phoneNumber: profile.phoneNumber,
        birthday: birthdayString,
        address: profile.address,
        city: profile.city,
        zipCode: profile.zipCode
      });
      this.profilePictureUrl = profile.imagePath ? `http://localhost:8080${profile.imagePath}` : null;
    });
  }

  convertToDate(dateArray: any): Date {
    if (Array.isArray(dateArray)) {
      return new Date(dateArray[0], dateArray[1] - 1, dateArray[2], dateArray[3] || 0, dateArray[4] || 0, dateArray[5] || 0);
    }
    return new Date(dateArray);
  }

  onSubmit() {
    if (this.profileForm.valid) {
      const formValue = this.profileForm.value;
  
      const profileDTO: any = {
        name: formValue.name,
        surname: formValue.surname,
        phoneNumber: formValue.phoneNumber,
        birthday: formValue.birthday,
        address: formValue.address,
        city: formValue.city,
        zipCode: formValue.zipCode
      };
  
      if (formValue.oldPassword || formValue.newPassword || formValue.confirmNewPassword) {
        if (!formValue.oldPassword) {
          this.showAlert('Error', 'Old password is required.');
          return;
        }
        if (!formValue.newPassword) {
          this.showAlert('Error', 'New password is required.');
          return;
        }
        if (!formValue.confirmNewPassword) {
          this.showAlert('Error', 'Confirm new password is required.');
          return;
        }
        if (formValue.newPassword !== formValue.confirmNewPassword) {
          this.showAlert('Error', 'New passwords do not match.');
          return;
        }
        profileDTO.oldPassword = formValue.oldPassword;
        profileDTO.newPassword = formValue.newPassword;
        profileDTO.confirmNewPassword = formValue.confirmNewPassword;
      }
  
      this.profileService.updateProfile(profileDTO).subscribe(
        (response: any) => {
          this.dialog.open(AlertDialogComponent, {
            data: {
              title: 'Success',
              message: 'Profile updated successfully'
            },
            width: '300px',
            disableClose: true,
            backdropClass: 'backdrop-no-click'
          });
          this.profile = response;
          if (this.selectedFile) {
            this.uploadProfilePicture();
          } else {
            this.profilePictureUrl = response.imagePath ? `http://localhost:8080${response.imagePath}` : null;
            this.resetPasswordFields();
          }
        },
        (error: HttpErrorResponse) => {
          console.error('Error:', error);
          if (error.status === 400 && error.error.includes('Invalid credentials')) {
            this.showAlert('Error', 'Old password is incorrect.');
          } else if (error.status === 401) {
            this.showAlert('Error', 'Unauthorized. Please login again.');
          } else {
            this.showAlert('Error', 'Error updating profile: ' + error.message);
          }
        }
      );
    } else {
      this.showAlert('Error', 'Form is invalid. Please check all fields.');
    }
  }

  uploadProfilePicture() {
    if (this.selectedFile) {
      this.profileService.uploadProfilePicture(this.selectedFile).subscribe(
        (response: any) => {
          this.profile.imageId = response.imageId;
          this.profile.imagePath = response.imagePath;
          this.profilePictureUrl = response.imagePath ? `http://localhost:8080${response.imagePath}` : null;
          this.resetPasswordFields();
        },
        (error: HttpErrorResponse) => {
          console.error('Error:', error);
          this.showAlert('Error', 'Error uploading profile picture: ' + error.message);
        }
      );
    }
  }

  passwordsMatchValidator(control: AbstractControl) {
    const newPassword = control.get('newPassword')?.value;
    const confirmNewPassword = control.get('confirmNewPassword')?.value;
    return newPassword === confirmNewPassword ? null : { mismatch: true };
  }

  resetPasswordFields() {
    this.profileForm.patchValue({
      oldPassword: '',
      newPassword: '',
      confirmNewPassword: ''
    });
  }

  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = () => {
        this.profilePictureUrl = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }

  showAlert(title: string, message: string) {
    this.dialog.open(AlertDialogComponent, {
      data: {
        title: title,
        message: message
      },
      width: '300px',
      disableClose: true,
      backdropClass: 'backdrop-no-click'
    });
  }

  toggleShowOldPassword() {
    this.showOldPassword = !this.showOldPassword;
  }

  toggleShowNewPassword() {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleShowConfirmNewPassword() {
    this.showConfirmNewPassword = !this.showConfirmNewPassword;
  }
}
