import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { trigger, transition, style, animate, query, group } from '@angular/animations';
import { NavbarComponent } from './navbar/navbar';
import { MapComponent } from './map/map';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, MapComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
  animations: [
    trigger('routeAnimations', [
      transition('login => register', [
        query(':enter, :leave', [
          style({
            position: 'absolute',
            width: '100%',
            height: '100%'
          })
        ], { optional: true }),
        query(':enter .object', [
          style({ opacity: 0 })
        ], { optional: true }),
        query(':enter', [
          style({ opacity: 1 })
        ], { optional: true }),
        group([
          query(':leave .object', [
            style({ marginLeft: '-30vw', marginRight: '0', transform: 'translateX(0)' }),
            animate('800ms cubic-bezier(0.68, -0.55, 0.265, 1.55)', 
              style({ marginLeft: '0', marginRight: '-30vw', transform: 'translateX(calc(100vw - 80%))' }))
          ], { optional: true }),
        ])
      ]),  
      transition('register => login', [
        query(':enter, :leave', [
          style({
            position: 'absolute',
            width: '100%',
            height: '100%'
          })
        ], { optional: true }),
        query(':enter .object', [
          style({ opacity: 0 })
        ], { optional: true }),
        query(':enter', [
          style({ opacity: 1 })
        ], { optional: true }),
        group([
          query(':leave .object', [
            style({ marginRight: '-30vw', marginLeft: '0', transform: 'translateX(0)' }),
            animate('800ms cubic-bezier(0.68, -0.55, 0.265, 1.55)', 
              style({ marginRight: '0', marginLeft: '-30vw', transform: 'translateX(calc(-100vw + 80%))' }))
          ], { optional: true }),
        ])
      ])
    ])
  ]
})
export class App {
  protected readonly title = signal('WEB');

  prepareRoute(outlet: RouterOutlet) {
    return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
  }
}