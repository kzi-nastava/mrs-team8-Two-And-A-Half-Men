import { Component, input, output } from '@angular/core';
import {CommonModule} from '@angular/common';

export interface TabItem {
	id: string;
	label: string;
	icon?: string; // SVG path or icon identifier
}

@Component({
	selector: 'app-tab-navigation',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './tab-navigation.component.html',
	styleUrls: ['./tab-navigation.component.css']
})
export class TabNavigationComponent {
	// Inputs
	tabs = input.required<TabItem[]>();
	activeTab = input.required<string>();

	// Outputs
	tabChange = output<string>();

	onTabClick(tabId: string): void {
		this.tabChange.emit(tabId);
	}
}
