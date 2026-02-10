import {
	Component,
	Input,
	Output,
	EventEmitter,
	OnInit,
	OnDestroy,
	effect,
	inject,
	signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Message } from '@features/chat/models/message.model';
import { SupportChatService } from '@features/chat/services/support-chat.service';
import { AuthService } from '@core/services/auth.service';
import { WebSocketService } from '@core/services/web-socket.service';

@Component({
	selector: 'app-chat-window',
	standalone: true,
	imports: [CommonModule, FormsModule],
	templateUrl: './chat-window.component.html',
	styleUrls: ['./chat-window.component.css'],
})
export class ChatWindowComponent implements OnInit, OnDestroy {
	@Input() chatId!: number;
	@Input() title: string = 'Live support';
	@Input() isOpen: boolean = true;
	@Input() isMinimizable: boolean = false;

	@Output() isOpenChange = new EventEmitter<boolean>();
	@Output() minimize = new EventEmitter<void>();
	@Output() close = new EventEmitter<void>();

	private chatService = inject(SupportChatService);
	private authService = inject(AuthService);
	private webSocketService = inject(WebSocketService);

	messages = signal<Message[]>([]);
	newMessage = signal<string>('');
	currentUserId = signal<number>(0);
	isLoading = signal<boolean>(false);

	private unsubscribeFn: (() => void) | null = null;

	constructor() {
		effect(() => {
			const user = this.authService.user();
			if (user) {
				this.currentUserId.set(user.id);
			}
		});
	}

	ngOnInit() {
		this.loadMessages();
		this.subscribeToMessages();
	}

	ngOnDestroy() {
		this.unsubscribeFromMessages();
	}

	private loadMessages() {
		this.isLoading.set(true);
		this.chatService.getChatMessages(this.chatId).subscribe({
			next: (messages) => {
				this.messages.set(messages);
				setTimeout(() => this.scrollToBottom(), 100);
				this.isLoading.set(false);
				this.markAsRead();
			},
			error: (error) => {
				console.error('[ChatWindow] Error loading messages:', error);
				this.isLoading.set(false);
			},
		});
	}

	private async subscribeToMessages() {
		this.unsubscribeFromMessages();

		try {
			this.unsubscribeFn = await this.webSocketService.subscribe<Message>(
				`/topic/chat/${this.chatId}`,
				(message) => this.handleNewMessage(message),
			);
			console.log(`[ChatWindow] Subscribed to chat ${this.chatId}`);
		} catch (error) {
			console.error('[ChatWindow] Error subscribing to messages:', error);
		}
	}

	private unsubscribeFromMessages() {
		if (this.unsubscribeFn) {
			console.log(`[ChatWindow] Unsubscribing from chat ${this.chatId}`);
			this.unsubscribeFn();
			this.unsubscribeFn = null;
		}
	}

	private handleNewMessage(message: Message) {
		console.log('[ChatWindow] New message received:', message);
		message.timestamp = new Date(message.timestamp);
		this.messages.update((msgs) => [...msgs, message]);
		setTimeout(() => this.scrollToBottom(), 100);

		if (message.senderId !== this.currentUserId()) {
			this.markAsRead();
		}
	}

	onSend() {
		const content = this.newMessage().trim();
		if (!content) return;

		this.chatService.sendMessage(this.chatId, content).subscribe({
			next: () => {
				this.newMessage.set('');
			},
			error: (error) => {
				console.error('[ChatWindow] Error sending message:', error);
			},
		});
	}

	onMinimize() {
		this.minimize.emit();
	}

	onClose() {
		this.isOpen = false;
		this.isOpenChange.emit(false);
		this.close.emit();
	}

	isOwnMessage(message: Message): boolean {
		return message.senderId === this.currentUserId();
	}

	formatTime(timestamp: Date): string {
		const date = new Date(timestamp);
		const hours = date.getHours().toString().padStart(2, '0');
		const minutes = date.getMinutes().toString().padStart(2, '0');
		return `${hours}:${minutes}`;
	}

	private scrollToBottom() {
		const messagesContainer = document.querySelector('.chat-messages');
		if (messagesContainer) {
			messagesContainer.scrollTop = messagesContainer.scrollHeight;
		}
	}

	private markAsRead() {
		this.chatService.markMessagesAsRead(this.chatId).subscribe({
			error: (error) => {
				console.error('[ChatWindow] Error marking messages as read:', error);
			},
		});
	}
}
