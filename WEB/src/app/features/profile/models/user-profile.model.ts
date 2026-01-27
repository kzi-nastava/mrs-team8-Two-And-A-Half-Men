import {PersonalInfo} from '@shared/models/personal-info.model';
import {VehicleInfo} from '@shared/models/vehicles.model';
import {PendingChangeRequest} from '@shared/models/profile-change-request.model';

export interface UserProfile {
	personalInfo: PersonalInfo;
	vehicleInfo?: VehicleInfo;
	pendingChangeRequest?: PendingChangeRequest;
}
export interface PasswordChange {
	oldPassword: string;
	newPassword: string;
	confirmPassword: string;
}
