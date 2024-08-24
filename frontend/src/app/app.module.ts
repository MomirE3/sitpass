import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RegistrationComponent } from './registration/registration.component';
import { FacilitiesComponent } from './facilities/facilities.component';
import { HeaderComponent } from './header/header.component';
import { AuthInterceptor } from './auth-interceptor.service';
import { AccountRequestsComponent } from './admin/account-requests/account-requests.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RejectDialogComponent } from './reject-dialog/reject-dialog.component';
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';
import { ProfileComponent } from './profile/profile.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { AlertDialogComponent } from './alert-dialog/alert-dialog.component';
import { FacilityDetailsComponent } from './facility-details/facility-details.component';
import { CreateFacilityComponent } from './create-facility/create-facility.component';
import { AddReviewComponent } from './add-review/add-review.component';
import { AddManagerComponent } from './add-manager/add-manager.component';
import { RemoveManagerComponent } from './remove-manager/remove-manager.component';
import { UpdateFacilityComponent } from './update-facility/update-facility.component';
import { ReviewsComponent } from './reviews/reviews.component';
import { MyReviewsComponent } from './my-reviews/my-reviews.component';
import { CommentItemComponent } from './comment-item/comment-item.component';
import { FilterByParentPipe } from './reviews/filter-by-parent-pipe';
import { AnalyticsComponent } from './analytics/analytics.component';
import { NgxEchartsModule } from 'ngx-echarts';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegistrationComponent,
    FacilitiesComponent,
    HeaderComponent,
    AccountRequestsComponent,
    RejectDialogComponent,
    ConfirmDialogComponent,
    ProfileComponent,
    AlertDialogComponent,
    FacilityDetailsComponent,
    CreateFacilityComponent,
    AddReviewComponent,
    AddManagerComponent,
    RemoveManagerComponent,
    UpdateFacilityComponent,
    ReviewsComponent,
    MyReviewsComponent,
    CommentItemComponent,
    FilterByParentPipe,
    AnalyticsComponent
    ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,  
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts')
    }),
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true } 
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
