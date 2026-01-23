import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

interface RideStop {
  name: string;
  completed?: boolean;
  current?: boolean;
}

interface RideData {
  stops: RideStop[];
  remainingDistance: number;
  remainingTime: number;
}

@Component({
  selector: 'app-driver-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-ride-info-panel.html',
  styleUrls: ['./driver-ride-info-panel.css']
})
export class DriverRideInfoPanelComponent {
  @Input() currentRide!: RideData;
  @Output() endRide = new EventEmitter<void>();
  @Output() completeStop = new EventEmitter<RideStop>();

  onEndRide() {
    this.endRide.emit();
  }

  onCompleteStop(stop: RideStop) {
    this.completeStop.emit(stop);
  }
}