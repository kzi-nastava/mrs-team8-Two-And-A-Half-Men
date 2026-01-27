export interface BookedRide {
  id: number;
  startTime: string | null;
  scheduleTime: string;
  route: string;
  driverName: string;
  status: string;
}