import { Component } from '@angular/core';
import { RideInfoPanelComponent } from '@features/customer/ride-tracking/ride-info-panel/ride-info-panel.component';
import { MapComponent } from '@shared/components/map/map.component';

@Component({
	selector: 'app-customer-ride-tracking',
	standalone: true,
	imports: [RideInfoPanelComponent, MapComponent],
	templateUrl: './ride-tracking.component.html',
	styleUrls: ['./ride-tracking.component.css'],
})
export class RideTrackingComponent {}
