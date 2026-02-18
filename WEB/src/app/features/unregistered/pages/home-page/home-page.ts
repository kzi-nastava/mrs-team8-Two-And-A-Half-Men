import { Component } from '@angular/core';
import { EstimateForm } from "@shared/components/forms/estimate-form/estimate-form";
import { MapComponent } from '@shared/components/map/map.component';
import { BOOKING_MAP_CONFIG } from '@shared/components/map/map.config';


@Component({
  selector: 'app-home-page',
  imports: [EstimateForm, MapComponent],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css',
})
export class HomePage {
	mapConfig = BOOKING_MAP_CONFIG;
}
