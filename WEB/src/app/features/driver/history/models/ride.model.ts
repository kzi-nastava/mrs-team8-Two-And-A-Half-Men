export interface Ride {
  id: number;
  startTime: string;
  endTime: string;
  scheduledTime: string;

  driverName: string;
  rideOwnerName: string;

  status: string;
  path: string;
  cancellationReason: string;
  price: number;
  totalCost: number;

  passengersMails: string[];
}

export const MOCK_RIDES: Ride[] = [
  {
    id: 1,
    startTime: '2024-01-15T14:20:00',
    endTime: '2024-01-15T14:55:00',
    scheduledTime: '2024-01-15T14:15:00',
    driverName: 'Marko Marković',
    rideOwnerName: 'petar.petrovic@gmail.com',
    status: 'COMPLETED',
    path: 'Novi Sad → Beograd',
    cancellationReason: '',
    price: 1200,
    totalCost: 1350,
    passengersMails: [
      'petar.petrovic@gmail.com',
      'ana.petrovic@gmail.com'
    ]
  },
  {
    id: 2,
    startTime: '2024-02-02T09:05:00',
    endTime: '2024-02-02T09:42:00',
    scheduledTime: '2024-02-02T09:00:00',
    driverName: 'Nikola Nikolić',
    rideOwnerName: 'jelena.jovanovic@gmail.com',
    status: 'COMPLETED',
    path: 'Zemun → Slavija',
    cancellationReason: '',
    price: 850,
    totalCost: 950,
    passengersMails: [
      'jelena.jovanovic@gmail.com'
    ]
  },
  {
    id: 3,
    startTime: '',
    endTime: '',
    scheduledTime: '2024-02-10T18:30:00',
    driverName: 'Milan Ilić',
    rideOwnerName: 'ivan.ivanovic@gmail.com',
    status: 'CANCELLED',
    path: 'Dorćol → Novi Beograd',
    cancellationReason: 'Passenger did not show up',
    price: 0,
    totalCost: 0,
    passengersMails: [
      'ivan.ivanovic@gmail.com'
    ]
  },
  {
    id: 4,
    startTime: '2024-03-01T22:10:00',
    endTime: '2024-03-01T22:55:00',
    scheduledTime: '2024-03-01T22:00:00',
    driverName: 'Stefan Stefanović',
    rideOwnerName: 'marija.m@gmail.com',
    status: 'COMPLETED',
    path: 'Aerodrom → Centar',
    cancellationReason: '',
    price: 2200,
    totalCost: 2600,
    passengersMails: [
      'marija.m@gmail.com',
      'luka.m@gmail.com',
      'andrej.m@gmail.com'
    ]
  },
  {
    id: 5,
    startTime: '2024-03-10T07:45:00',
    endTime: '',
    scheduledTime: '2024-03-10T07:45:00',
    driverName: 'Petar Petrović',
    rideOwnerName: 'sona.s@gmail.com',
    status: 'STARTED',
    path: 'Limanski park → FTN',
    cancellationReason: '',
    price: 500,
    totalCost: 0,
    passengersMails: [
      'sona.s@gmail.com'
    ]
  }
];
