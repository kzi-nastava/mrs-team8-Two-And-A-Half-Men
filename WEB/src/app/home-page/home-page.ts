import { Component } from '@angular/core';
import { EstimateForm } from "../estimate-form/estimate-form";
import { MapComponent } from "../map/map";
import { RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-home-page',
  imports: [EstimateForm, MapComponent, RouterOutlet],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css',
})
export class HomePage {
}
