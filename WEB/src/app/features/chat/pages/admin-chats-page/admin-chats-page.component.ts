import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatWindowComponent } from '@features/chat/components/chat-window/chat-window.component';
import { SupportChatService } from '@features/chat/services/support-chat.service';
import { SupportChat } from '@features/chat/models/message.model';
import { interval } from 'rxjs';
import { startWith, switchMap } from 'rxjs/operators';

@Component({
	selector: 'app-admin-chats-page',
	standalone: true,
	imports: [CommonModule, ChatWindowComponent],
	templateUrl: './admin-chats-page.component.html',
	styleUrls: ['./admin-chats-page.component.css'],
})
export class AdminChatsPageComponent implements OnInit {
	private chatService = inject(SupportChatService);

	chats = signal<SupportChat[]>([]);
	selectedChat = signal<SupportChat | null>(null);
	isLoading = signal<boolean>(false);

	ngOnInit() {
		this.loadChats();

		// Refresh chats every 10 seconds
		interval(10000)
			.pipe(
				startWith(0),
				switchMap(() => this.chatService.getAllActiveChats())
			)
			.subscribe({
				next: (chats) => {
					this.chats.set(chats);
				},
				error: (error) => {
					console.error('[AdminChatsPage] Error loading chats:', error);
				}
			});
	}

	private loadChats() {
		this.isLoading.set(true);
		this.chatService.getAllActiveChats().subscribe({
			next: (chats) => {
				this.chats.set(chats);
				this.isLoading.set(false);
			},
			error: (error) => {
				console.error('[AdminChatsPage] Error loading chats:', error);
				this.isLoading.set(false);
			}
		});
	}

	selectChat(chat: SupportChat) {
		this.selectedChat.set(chat);
	}

	closeSelectedChat() {
		this.selectedChat.set(null);
	}

	getChatTitle(chat: SupportChat): string {
		return `${chat.userEmail || 'User'} (${chat.userType || 'Unknown'})`;
	}

	getLastMessagePreview(chat: SupportChat): string {
		if (!chat.lastMessage) return 'No messages yet';
		const content = chat.lastMessage.content;
		return content.length > 50 ? content.substring(0, 50) + '...' : content;
	}

	getLastMessageTime(chat: SupportChat): string {
		if (!chat.lastMessage) return '';
		const date = new Date(chat.lastMessage.timestamp);
		const hours = date.getHours().toString().padStart(2, '0');
		const minutes = date.getMinutes().toString().padStart(2, '0');
		return `${hours}:${minutes}`;
	}
}
