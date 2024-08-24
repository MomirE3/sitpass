import { Review } from "./review.model";

export interface VisitHistory {
  facilityId: number;
  facilityName: string;
  fromDateTime: Date;
  toDateTime: Date;
}

export interface Facility {
  id: number;
  name: string;
  city: string;
  address: string;
  description: string;
  totalRating: number;
}

export interface Profile {
  email: string;
  name: string;
  surname: string;
  phoneNumber: string;
  birthday: Date;
  address: string;
  city: string;
  zipCode: string;
  imageId?: number;
  imagePath?: string;
  oldPassword?: string;
  newPassword?: string;
  confirmNewPassword?: string;
  reviews?: Review[];
  managedFacilities?: Facility[];
  visitHistory?: VisitHistory[];
}
