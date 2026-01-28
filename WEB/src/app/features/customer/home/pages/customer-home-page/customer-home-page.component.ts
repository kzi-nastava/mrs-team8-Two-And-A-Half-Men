import { Component } from '@angular/core';
import { RouteForm } from '@shared/components/forms/route-form/route-form';
import { MapComponent } from '@shared/components/map/map.component';

@Component({
	selector: 'app-customer-home-page',
	imports: [RouteForm, MapComponent],
	templateUrl: './customer-home-page.component.html',
	styleUrl: './customer-home-page.component.css',
})
export class CustomerHomePageComponent {}
