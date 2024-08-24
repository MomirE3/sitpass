import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { AuthGuard } from './auth.guard';
import { AdminGuard } from './admin.guard'; 
import { AccountRequestsComponent } from './admin/account-requests/account-requests.component';
import { ProfileComponent } from './profile/profile.component';
import { FacilitiesComponent } from './facilities/facilities.component';
import { FacilityDetailsComponent } from './facility-details/facility-details.component';
import { CreateFacilityComponent } from './create-facility/create-facility.component'
import { AddReviewComponent } from './add-review/add-review.component';
import { AddManagerComponent } from './add-manager/add-manager.component';
import { RemoveManagerComponent } from './remove-manager/remove-manager.component';
import { UpdateFacilityComponent } from './update-facility/update-facility.component';
import { ReviewsComponent } from './reviews/reviews.component';
import { MyReviewsComponent } from './my-reviews/my-reviews.component';
import { AnalyticsComponent } from './analytics/analytics.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' }, 
  { path: 'facilities/:id/analytics', component: AnalyticsComponent },
  { path: 'add-review', component: AddReviewComponent, canActivate: [AuthGuard] }, 
  { path: 'facilities', component: FacilitiesComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: 'account-requests', component: AccountRequestsComponent, canActivate: [AdminGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'facilities/:id', component: FacilityDetailsComponent, canActivate: [AuthGuard] },
  { path: 'create-facility', component: CreateFacilityComponent, canActivate: [AdminGuard] },
  { path: 'add-manager', component: AddManagerComponent, canActivate: [AdminGuard] }, 
  { path: 'remove-manager', component: RemoveManagerComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'facilities/:id/update', component: UpdateFacilityComponent, canActivate: [AuthGuard] },
  { path: 'reviews', component: ReviewsComponent, canActivate: [AuthGuard] },
  { path: 'my-reviews', component: MyReviewsComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'login' } 
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
