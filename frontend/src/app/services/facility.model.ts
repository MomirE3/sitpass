export interface Facility {
  id: number;
  name: string;
  city: string;
  address: string;
  description: string;
  totalRating: number;
  disciplines: Discipline[];
  workDays: WorkDay[];
  images?: string[];
  imagesToDelete?: string[];
}

export interface Discipline {
  id: number;
  name: string;
}

export interface WorkDay {
  id: number;
  validFrom: string;
  day: string;
  from: string;
  until: string;
}
