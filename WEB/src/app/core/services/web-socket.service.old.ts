// import { effect, inject, Injectable } from '@angular/core';
// import SockJS from 'sockjs-client';
// import { CompatClient, Stomp, StompSubscription } from '@stomp/stompjs';
// import { PanicService } from '@shared/services/panic.service';
// import { DriverLocation } from '@shared/models/driver-location';
// import { DriverLocationWebsocketService } from '@shared/services/driver-location/driver-location-websocket.service';
// import { AuthService } from '@core/services/auth.service';
// import { LoggedInUserRole } from '@core/models/loggedInUser.model';
//
// @Injectable({
// 	providedIn: 'root',
// })
// export class OLDWebSocketService {
// 	private authService = inject(AuthService);
// 	private panicService = inject(PanicService);
// 	private driverLocationService = inject(DriverLocationWebsocketService);
//
// 	private stompClient: CompatClient | null = null;
// 	private isLoggedIn: boolean = false;
// 	private notificationSub?: StompSubscription;
//
// 	constructor() {
// 		this.listenForNotifications();
// 	}
//
// 	private listenForNotifications() {
// 		effect(() => {
// 			const user = this.authService.user(); // 👈 signal read
//
// 			if (!this.stompClient || !this.isLoggedIn) return;
//
// 			// 🔥 logout case
// 			if (!user) {
// 				this.notificationSub?.unsubscribe();
// 				this.notificationSub = undefined;
// 				return;
// 			}
//
// 			// 🔥 login case
// 			this.notificationSub?.unsubscribe(); // avoid duplicates
//
// 			console.log("subscribing to notifications for user", user.id);
// 			this.notificationSub = this.stompClient!.subscribe(
// 				`/topic/notifications/${user.id}`,
// 				(msg) => alert(msg),
// 			);
// 		});
// 	}
//
// 	connect() {
// 		const ws = new SockJS('/socket', null, {});
//
// 		this.stompClient = Stomp.over(ws);
// 		this.stompClient.debug = (str: any) => {
// 			console.log(str);
// 		};
//
// 		let that = this;
// 		this.stompClient.connect({}, function () {
// 			that.isLoggedIn = true;
// 			that.subscribeToPanic();
// 			that.subscribeToDriverLocations();
// 		});
// 	}
//
// 	private subscribeToPanic() {
// 		if (this.isLoggedIn && this.authService.user()?.role == LoggedInUserRole.ADMIN) {
// 			this.stompClient!.subscribe('/topic/panic', (message: any) => {
// 				console.log('Panic alert received:', message.body);
// 				if (message.body) {
// 					let data = JSON.parse(message.body);
// 					this.panicService.handlePanic(data);
// 				}
// 			});
// 		}
// 	}
//
// 	private subscribeToDriverLocations() {
// 		if (this.isLoggedIn) {
// 			this.stompClient!.subscribe('/topic/driver-locations', (message: any) => {
// 				if (message.body) {
// 					const location: DriverLocation = JSON.parse(message.body);
// 					console.log(location);
// 					this.driverLocationService.updateDriverLocation(location);
// 				}
// 			});
// 		}
// 	}
//
// 	private subscribeToSpecificDriverLocations(driverId: number) {
// 		if (this.isLoggedIn) {
// 			this.stompClient!.subscribe(`/topic/driver-locations/${driverId}`, (message: any) => {
// 				if (message.body) {
// 					const location: DriverLocation = JSON.parse(message.body);
// 					console.log(location);
// 					this.driverLocationService.updateDriverLocation(location);
// 				}
// 			});
// 		}
// 	}
//
// 	sendDriverLocation(location: { latitude: number; longitude: number }) {
// 		if (this.isLoggedIn && this.stompClient) {
// 			this.stompClient.send('/app/driver/location', {}, JSON.stringify(location));
// 		}
// 	}
//
// 	disconnect() {
// 		if (this.stompClient) {
// 			this.stompClient.disconnect();
// 			this.isLoggedIn = false;
// 		}
// 	}
// }
