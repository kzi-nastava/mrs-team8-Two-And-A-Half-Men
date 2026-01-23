// ride-info-panel.component.ts
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

interface RideStop {
  name: string;
  completed?: boolean;
  current?: boolean;
}

@Component({
  selector: 'app-ride-info-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ride-info-panel.html',
  styleUrls: ['./ride-info-panel.css']
})
export class RideInfoPanelComponent {
  @Input() stops: RideStop[] = [];
  @Input() remainingDistance: number = 0;
  @Input() remainingTime: number = 0;
  @Output() saveNote = new EventEmitter<string>();
  @Output() panic = new EventEmitter<void>();

  noteText: string = '';
  showNoteInput: boolean = false; // DODAJ OVO

  toggleNoteInput() {
    this.showNoteInput = !this.showNoteInput;
  }

  onSave() {
    this.saveNote.emit(this.noteText);
    this.noteText = ''; // Očisti nakon save
    this.showNoteInput = false; // Sakrij nakon save
  }

  onPanicClick() {
    this.panic.emit();
  }
}