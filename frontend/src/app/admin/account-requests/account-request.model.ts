export interface AccountRequest {
  id: number;
  email: string;
  password: string | null;
  createdAt: Date | null;
  address: string;
  status: string;
  rejectionReason: string;
}
