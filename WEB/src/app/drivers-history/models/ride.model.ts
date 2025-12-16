export interface Ride {
  id?: string;
  startedAt?: Date;
  endedAt?: Date;
  scheduledAt?: Date;
  status?: string;
  path?: string;
  cancelationReason?: string;
  cost?: number;
  userEmail?: string;
  passangersNumber?: number;
}