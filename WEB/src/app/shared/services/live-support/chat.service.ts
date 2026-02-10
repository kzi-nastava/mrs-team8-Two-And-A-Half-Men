import { computed, effect, inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Message, SupportChat, SendMessageRequest } from '@features/chat/models/message.model';
import { WebSocketService } from '@core/services/web-socket.service';
import { AuthService } from '@core/services/auth.service';
import { Router } from '@angular/router';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

@Injectable({
	providedIn: 'root',
})
export class ChatService {
	private readonly API_URL = '/api/v1/support';
	private http = inject(HttpClient);
	private webSocketService = inject(WebSocketService);
	private authService = inject(AuthService);

	chats = signal<SupportChat[]>([]);

	myChat = computed<SupportChat | null>(() => (this.chats().length > 0 ? this.chats()[0] : null));

	private unsubscribeFn: (() => void) | null = null;

	private subscribedUserId: number | null = null;

	constructor() {
		this.setupLiveSupportSubscription();
	}

	/**
	 * Setup effect to subscribe/unsubscribe based on user login
	 */
	private setupLiveSupportSubscription(): void {
		effect(() => {
			const user = this.authService.user();

			// logout case
			if (!user) {
				if (this.subscribedUserId !== null) {
					this.unsubscribeFromLiveSupport();
					this.chats.set([]);
					this.subscribedUserId = null;
				}
				return;
			}

			// 🔥 prevent resubscribe loop
			if (this.subscribedUserId === user.id) {
				return;
			}

			this.subscribedUserId = user.id;

			if (user.role === LoggedInUserRole.ADMIN) {
				this.subscribeToAdminSupport();
				this.getAllActiveChats().subscribe({
					next: (chats) => {
						this.chats.set(chats);
					},
					error: (error) => {
						console.error('[ChatService] Error loading admin chats:', error);
					},
				});
			} else {
				this.subscribeToUserSupport(user.id).then();
				this.getMyChat().subscribe({
					next: (chat) => {
						this.chats.set([chat]);
					},
					error: (error) => {
						console.error('[ChatService] Error loading user chat:', error);
					},
				});
			}
		});
	}

	/**
	 * Subscribe to user-specific notifications via WebSocket
	 */
	private async subscribeToAdminSupport(): Promise<void> {
		// Unsubscribe from previous subscription if exists
		this.unsubscribeFromLiveSupport();

		try {
			this.unsubscribeFn = await this.webSocketService.subscribe<Message>(
				`/topic/support/admin`,
				(message) => this.handleNewMessage(message),
			);
		} catch (error) {
			console.error('[ChatService] Error subscribing to live support:', error);
		}
	}

	/**
	 * Subscribe to user-specific notifications via WebSocket
	 */
	private async subscribeToUserSupport(userId: number): Promise<void> {
		// Unsubscribe from previous subscription if exists
		this.unsubscribeFromLiveSupport();

		try {
			this.unsubscribeFn = await this.webSocketService.subscribe<Message>(
				`/topic/chat/${userId}`,
				(message) => this.handleNewMessage(message),
			);
		} catch (error) {
			console.error('[ChatService] Error subscribing to live support:', error);
		}
	}

	/**
	 * Handle incoming notification from WebSocket
	 */
	private handleNewMessage(newMessage: Message): void {
		console.log('[CharService] New message received:', newMessage);
		const chatIndex = this.chats().findIndex((chat) => chat.id === newMessage.chatId);
		if (chatIndex === -1) {
			this.getChatById(newMessage.chatId).subscribe({
				next: (chat) => {
					this.chats.update((chats) => [...chats, chat]);
				},
				error: (error) => {
					console.error(`[ChatService] Error fetching chat ${newMessage.chatId}:`, error);
				},
			});
			return;
		}
		// Add to notifications list (prepend for newest first)
		this.chats.update((chats) => {
			const updatedChats = [...chats];
			updatedChats[chatIndex] = {
				...updatedChats[chatIndex],
				messages: [...updatedChats[chatIndex].messages, newMessage],
			};
			return updatedChats;
		});
	}

	/**
	 * Unsubscribe from notifications
	 */
	private unsubscribeFromLiveSupport(): void {
		if (this.unsubscribeFn) {
			console.log('[NotificationService] Unsubscribing from notifications');
			this.unsubscribeFn();
			this.unsubscribeFn = null;
		}
	}

	getMyChat(): Observable<SupportChat> {
		return this.http.get<SupportChat>(`${this.API_URL}/my-chat`);
	}
	getAllActiveChats(): Observable<SupportChat[]> {
		return this.http
			.get<SupportChat[]>(`${this.API_URL}/chats`)
			.pipe(map((chats) => chats.map((chat) => chat)));
	}

	getChatById(chatId: number): Observable<SupportChat> {
		return this.http.get<SupportChat>(`${this.API_URL}/chats/${chatId}`);
	}

	getChatMessages(chatId: number): Observable<Message[]> {
		return this.http
			.get<Message[]>(`${this.API_URL}/chats/${chatId}/messages`)
			.pipe(map((messages) => messages.map((msg) => this.convertMessageDates(msg))));
	}

	sendMessage(chatId: number, content: string): Observable<Message> {
		const request: SendMessageRequest = { content };
		return this.http
			.post<Message>(`${this.API_URL}/chats/${chatId}/messages`, request)
			.pipe(map((msg) => this.convertMessageDates(msg)));
	}

	markMessagesAsRead(chatId: number): Observable<void> {
		return this.http.put<void>(`${this.API_URL}/chats/${chatId}/mark-read`, {});
	}

	closeChat(chatId: number): Observable<void> {
		return this.http.put<void>(`${this.API_URL}/chats/${chatId}/close`, {});
	}

	reopenChat(chatId: number): Observable<void> {
		return this.http.put<void>(`${this.API_URL}/chats/${chatId}/reopen`, {});
	}

	private convertMessageDates(message: Message): Message {
		return {
			...message,
			timestamp: new Date(message.timestamp),
		};
	}
}
