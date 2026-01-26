import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
	selector: 'app-tab-content',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './tab-content.component.html',
	styleUrls: ['./tab-content.component.css']
})
export class TabContentComponent {
	// The ID of this tab
	tabId = input.required<string>();

	// The currently active tab
	activeTab = input.required<string>();

	// Whether this tab should be shown
	get isActive(): boolean {
		return this.tabId() === this.activeTab();
	}
}
