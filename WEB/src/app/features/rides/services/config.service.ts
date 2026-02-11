import { Injectable } from '@angular/core';
import { RideActions } from '@features/rides/models/config.model';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';
import { Ride } from '@shared/models/ride.model';

@Injectable({
	providedIn: 'root',
})
export class ConfigService {
	getActions(user: LoggedInUserRole | null, ride: Ride) {

	}
}
