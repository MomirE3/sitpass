export interface Review {
  facilityId: number;
  facilityName: string;
  equipmentRating: number;
  staffRating: number;
  hygieneRating: number;
  spaceRating: number;
  comment?: string;
  hidden: boolean;
}