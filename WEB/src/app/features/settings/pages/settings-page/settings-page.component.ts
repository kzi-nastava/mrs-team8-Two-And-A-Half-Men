import { Component, signal } from '@angular/core';
import { BoxDirective } from '@shared/directives/box/box.directive';
import { RidesListComponent } from '@shared/components/rides/ride-list/ride-list.component';
import { TabContentComponent } from '@shared/components/tabs/tab-content/tab-content.component';
import { TabIconDirective } from '@shared/directives/tab-icon/tab-icon.directive';
import { TabItem, TabNavigationComponent } from '@shared/components/tabs/tab-navigation/tab-navigation.component';
import {
	VehiclePricingSettingsComponent
} from '@features/settings/components/vehicle-pricing-settings/vehicle-pricing-settings.component';

@Component({
	selector: 'app-settings-page',
	imports: [
		TabContentComponent,
		TabIconDirective,
		TabNavigationComponent,
		VehiclePricingSettingsComponent,
	],
	templateUrl: './settings-page.component.html',
	styleUrl: './settings-page.component.css',
})
export class SettingsPageComponent {
	tabs: TabItem[] = [
		{ id: 'prices', label: 'Prices' },
	];
	activeTab = signal<string>('prices');

	setActiveTab(tabId: string) {
		this.activeTab.set(tabId);
	}
}
