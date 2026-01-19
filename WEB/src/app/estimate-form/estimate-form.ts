import { Component, effect, OnInit, signal } from '@angular/core';
import { FormGroup, Validators, FormBuilder } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';


interface NominatimResult {
  place_id: number;
  display_name: string;
  lat: string;
  lon: string;
}

@Component({
  selector: 'app-estimate-form',
  imports: [ReactiveFormsModule, CommonModule, RouterModule, HttpClientModule],
  templateUrl: './estimate-form.html',
  styleUrl: './estimate-form.css',
})
export class EstimateForm {
  estimateForm: FormGroup;
    
  
  
  startQuery = signal('');
  endQuery = signal('');

  startSuggestions = signal<NominatimResult[]>([]);
  endSuggestions = signal<NominatimResult[]>([]);

  showStartSuggestions = signal(false);
  showEndSuggestions = signal(false);
  estimatedTime = signal<number | null>(null);
  constructor(
    private fb: FormBuilder,
    private http: HttpClient
  ) {
    this.estimateForm = this.fb.group({
      startpoint: ['', Validators.required],
      endpoint: ['', Validators.required],
    });
     effect((onCleanup) => {
      const query = this.startQuery();
      if (query.length > 3) {
        this.showStartSuggestions.set(false);
        const sub = this.searchLocation(query).subscribe(results => {
          if(results.length === 1 && this.startQuery() === results[0].display_name) {
            this.showStartSuggestions.set(false);
          }
          this.startSuggestions.set(results);
          this.showStartSuggestions.set(results.length > 0);
        });
        onCleanup(() => sub.unsubscribe());
      } else {
        this.startSuggestions.set([]);
        this.showStartSuggestions.set(false);
      }
    });
    effect((onCleanup) => {
      const query = this.endQuery();
      if (query.length > 3) {
        this.showEndSuggestions.set(false);
        const sub = this.searchLocation(query).subscribe(results => {
          if(results.length === 1 && this.endQuery() === results[0].display_name) {
            this.showEndSuggestions.set(false);
          }
          this.endSuggestions.set(results);
          this.showEndSuggestions.set(results.length > 0);
        });
        onCleanup(() => sub.unsubscribe());
      } else {
        this.endSuggestions.set([]);
        this.showEndSuggestions.set(false);
      }
    });
  }

   


  searchLocation(query: string) {
    const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=5`;
    return this.http.get<NominatimResult[]>(url, {
      headers: {
        'User-Agent': 'RouteEstimateApp/1.0'
      }
    });
  }



  selectStartSuggestion(suggestion: NominatimResult) {
    this.startQuery.set(suggestion.display_name);
    this.showStartSuggestions.set(false);
    this.startSuggestions.set([]);
    // Set it on a mpap
  }

  selectEndSuggestion(suggestion: NominatimResult) {
    this.endQuery.set(suggestion.display_name);
    this.showEndSuggestions.set(false);
    this.endSuggestions.set([]);
    //Set it on a map
  }

  onSubmit() {
    if (this.estimateForm.valid) {
      const startpoint = this.estimateForm.get('startpoint')?.value ?? '';
      const endpoint = this.estimateForm.get('endpoint')?.value ?? '';
      console.log('Estimate Form Submitted', { startpoint, endpoint });
            alert(`Route from:\n${startpoint}\n\nTo:\n${endpoint}`);
            this.estimatedTime.set(42); 
    } else {
      console.log('Form is invalid');
    }
  }

  onBlur(field: 'start' | 'end') {
    setTimeout(() => {
      if (field === 'start') {
        this.showStartSuggestions.set(false);
      } else {
        this.showEndSuggestions.set(false);
      }
    }, 200);
  }
  onReset() {
    this.estimateForm.reset();
    this.startQuery.set('');
    this.endQuery.set('');
    this.startSuggestions.set([]);
    this.endSuggestions.set([]);
    this.showStartSuggestions.set(false);
    this.showEndSuggestions.set(false);
  }
}