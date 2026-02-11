import { Injectable } from '@angular/core';
import {
	ACTIONS,
	ADMIN,
	DRIVER,
	NO_ACTIONS,
	PASSENGER,
	RIDE_OWNER,
	RideActions,
} from '@features/rides/models/config.model';
import { LoggedInUser, LoggedInUserRole } from '@core/models/loggedInUser.model';
import { Ride } from '@shared/models/ride.model';


@Injectable({
	providedIn: 'root',
})
export class ConfigService {
	getActions(user: LoggedInUser | null, ride: Ride | null): RideActions{
		if (!ride) {
			return NO_ACTIONS;
		}
		if (!user) {
			return ACTIONS[PASSENGER][ride.status];
		}
		if (user.role === LoggedInUserRole.ADMIN) {
			return ACTIONS[ADMIN][ride.status];
		}
		if (user.role === LoggedInUserRole.DRIVER) {
			return ACTIONS[DRIVER][ride.status];
		}
		if (user.id === ride.rideOwnerId) {
			return ACTIONS[RIDE_OWNER][ride.status];
		}
		return ACTIONS[PASSENGER][ride.status];
	}
}

