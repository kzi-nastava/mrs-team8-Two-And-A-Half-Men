import {
	Component,
	input,
	output,
	ContentChildren,
	QueryList,
	AfterContentInit
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { TabIconDirective } from '@shared/directives/tab-icon/tab-icon.directive';

export type TabAlignment = 'left' | 'right' | 'center' | 'space-between' | 'split';

export interface TabItem {
	id: string;
	label: string;
	position?: 'left' | 'right'; // Only used when alignment is 'split'
}

@Component({
	selector: 'app-tab-navigation',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './tab-navigation.component.html',
	styleUrls: ['./tab-navigation.component.css']
})
export class TabNavigationComponent implements AfterContentInit {
	// Inputs
	tabs = input.required<TabItem[]>();
	activeTab = input.required<string>();
	alignment = input<TabAlignment>('left'); // Default alignment

	// Outputs
	tabChange = output<string>();

	// Get all projected icons
	@ContentChildren(TabIconDirective)
	icons!: QueryList<TabIconDirective>;

	private iconMap = new Map<string, TabIconDirective>();

	ngAfterContentInit() {
		// Map icons to their tab IDs
		this.icons.forEach(icon => {
			this.iconMap.set(icon.forTab, icon);
		});
	}

	onTabClick(tabId: string): void {
		this.tabChange.emit(tabId);
	}

	getIconForTab(tabId: string): TabIconDirective | undefined {
		return this.iconMap.get(tabId);
	}

	// Get tabs for left side (when using split alignment)
	get leftTabs(): TabItem[] {
		if (this.alignment() !== 'split') return [];
		return this.tabs().filter(tab => tab.position === 'left' || !tab.position);
	}

	// Get tabs for right side (when using split alignment)
	get rightTabs(): TabItem[] {
		if (this.alignment() !== 'split') return [];
		return this.tabs().filter(tab => tab.position === 'right');
	}

	// Get CSS class based on alignment
	get alignmentClass(): string {
		return `align-${this.alignment()}`;
	}
}
