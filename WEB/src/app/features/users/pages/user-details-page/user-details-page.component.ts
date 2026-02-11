import { Component, inject, OnInit, signal } from '@angular/core';
import {
	PersonalInfo,
	UserDetailResponse,
	VehicleInfo,
} from '@features/users/models/user.model';
import { PendingChangeRequest } from '@shared/models/profile-change-request.model';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminUserService } from '@features/users/services/admin-user.service';
import { BoxDirective } from '@shared/directives/box/box.directive';
import { RequestChangePreviewComponent } from '@shared/components/forms/request-change-preview/request-change-preview.component';
import { PersonalInfoFormComponent } from '@shared/components/forms/personal-info-form/personal-info-form.component';
import { TabContentComponent } from '@shared/components/tabs/tab-content/tab-content.component';
import { TabIconDirective } from '@shared/directives/tab-icon/tab-icon.directive';
import {
	TabItem,
	TabNavigationComponent,
} from '@shared/components/tabs/tab-navigation/tab-navigation.component';
import { VehicleInfoFormComponent } from '@shared/components/forms/vehicle-info-form/vehicle-info-form.component';
import { AdditionalService, VehicleType } from '@shared/models/vehicles.model';
import { VehiclesService } from '@shared/services/vehicles/vehicles.service';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-user-details-page',
	imports: [
		BoxDirective,
		RequestChangePreviewComponent,
		PersonalInfoFormComponent,
		TabContentComponent,
		TabIconDirective,
		TabNavigationComponent,
		VehicleInfoFormComponent,
	],
	providers: [AdminUserService],
	templateUrl: './user-details-page.component.html',
	styleUrl: './user-details-page.component.css',
})
export class UserDetailsPageComponent implements OnInit {
	userId: number | null = null;
	isLoading = signal(false);
	activeTab = signal<string>('personal');

	private personalTab: TabItem = { id: 'personal', label: 'Personal data', position: 'left' };
	private vehicleTab: TabItem = { id: 'vehicle', label: 'Vehicle data', position: 'left' };
	get tabs(): TabItem[] {
		const tabs: TabItem[] = [this.personalTab];
		if (this.vehicleInfo()) {
			tabs.push(this.vehicleTab);
		}
		return tabs;
	}

	personalInfo = signal<PersonalInfo | null>(null);
	vehicleInfo = signal<VehicleInfo | null>(null);
	pendingChangeRequest = signal<PendingChangeRequest | null>(null);

	// Options from backend
	vehicleTypes = signal<VehicleType[]>([]);
	availableServices = signal<AdditionalService[]>([]);

	isProcessing = signal(false);

	private route = inject(ActivatedRoute);
	private router = inject(Router);
	private usersService = inject(AdminUserService);
	private vehicleService = inject(VehiclesService);
	private popupsService = inject(PopupsService);

	ngOnInit(): void {
		try {
			const id = this.route.snapshot.paramMap.get('userId');
			if (!id) {
				this.router
					.navigate(['/error/not-found'], { queryParams: { msg: 'User not found' } })
					.then();
				return;
			}
			this.userId = parseInt(id, 10);
			if (isNaN(this.userId)) {
				this.router
					.navigate(['/error/not-found'], {
						queryParams: { msg: 'User id not a number' },
					})
					.then();
				return;
			}
			this.loadUserDetails();
		} catch (error: any) {}
	}

	loadUserDetails(): void {
		if (!this.userId) return;

		this.isLoading.set(true);

		this.usersService.getUserDetails(this.userId).subscribe({
			next: (response: UserDetailResponse) => {
				this.personalInfo.set(response.personalInfo);
				this.vehicleInfo.set(response.vehicleInfo || null);
				this.pendingChangeRequest.set(response.pendingChangeRequest || null);
				if (response.vehicleInfo) {
					this.loadVehicleOptions();
				}
				this.isLoading.set(false);
			},
			error: (err) => {
				console.error('Error loading user details:', err);
				this.isLoading.set(false);
				this.popupsService.error(
					'Error',
					'Failed to load user details. Please try again later.',
					{
						onConfirm: () => this.router.navigate(['users']).then(),
						buttonText: 'Go Back',
					},
				);
			},
		});
	}

	loadVehicleOptions() {
		this.vehicleService.getVehicleOptions().subscribe({
			next: (options) => {
				this.vehicleTypes.set(options.vehicleTypes);
				this.availableServices.set(options.additionalServices);
			},
			error: (err) => {
				console.error('Failed to load vehicle options', err);
			},
		});
	}

	goBack(): void {
		this.router.navigate(['users']).then();
	}

	openChat(): void {
		// TODO: Implement chat functionality
		alert('Chat feature coming soon!');
	}

	onTabChange(tabId: string): void {
		this.activeTab.set(tabId);
	}

	blockUser(): void {
		if (!this.userId) return;

		this.popupsService.prompt(
			'Block User',
			'Please provide a reason for blocking this user:',
			'Reason for blocking',
			(reason) => {
				this.isProcessing.set(true);
				this.usersService.blockUser(this.userId!, reason).subscribe({
					next: () => {
						this.isProcessing.set(false);
						// Reload user details to get updated block status
						this.loadUserDetails();
						this.popupsService.success(
							'User Blocked',
							'The user has been blocked successfully.',
						);
					},
					error: (err) => {
						console.error('Error blocking user:', err);
						this.isProcessing.set(false);
						this.popupsService.error(
							'Error',
							'Failed to block user. Please try again later.',
						);
					},
				});
			},
		);
	}

	unblockUser(): void {
		if (!this.userId) return;

		this.popupsService.confirm(
			'Unblock User',
			'Are you sure you want to unblock this user?',
			() => {
				this.isProcessing.set(true);
				this.usersService.unblockUser(this.userId!).subscribe({
					next: () => {
						this.isProcessing.set(false);
						// Reload user details to get updated block status
						this.loadUserDetails();
						this.popupsService.success(
							'User Unblocked',
							'The user has been unblocked successfully.',
						);
					},
					error: (err) => {
						console.error('Error unblocking user:', err);
						this.isProcessing.set(false);
						this.popupsService.error(
							'Error',
							'Failed to unblock user. Please try again later.',
						);
					},
				});
			},
		);
	}

	approveChangeRequest(): void {
		const request = this.pendingChangeRequest();
		if (!request) return;

		this.popupsService.confirm(
			'Approve Changes',
			'Are you sure you want to approve these changes? This action cannot be undone.',
			() => {
				this.isProcessing.set(true);
				this.usersService.approveChangeRequest(request.id).subscribe({
					next: () => {
						this.isProcessing.set(false);
						// Reload user details
						this.loadUserDetails();
						this.popupsService.success(
							'Changes Approved',
							'The changes have been approved successfully.',
						);
					},
					error: (err) => {
						this.popupsService.error(
							'Error',
							'Failed to approve changes. Please try again later.',
						);
						this.isProcessing.set(false);
						console.error('Error approving changes:', err);
					},
				});
			},
		);
	}

	rejectChangeRequest(): void {
		const request = this.pendingChangeRequest();
		if (!request) return;

		this.popupsService.confirm(
			'Reject Changes',
			'Are you sure you want to reject these changes? This action cannot be undone.',
			() => {
				this.isProcessing.set(true);
				this.usersService.rejectChangeRequest(request.id).subscribe({
					next: () => {
						this.isProcessing.set(false);
						// Reload user details
						this.loadUserDetails();
						this.popupsService.success('Changes Rejected', 'The changes have been rejected successfully.');
					},
					error: (err) => {
						console.error('Error rejecting changes:', err);
						this.isProcessing.set(false);
						this.popupsService.error('Error', 'Failed to reject changes. Please try again later.');
					},
				});
			},
		);
	}
}
