import { Component } from '@angular/core';
import { EstimateForm } from "../../../../estimate-form/estimate-form";
import { MapComponent } from "../../../../map/map";


@Component({
  selector: 'app-home-page',
  imports: [EstimateForm, MapComponent],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css',
})
export class HomePage {
}
