import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatWindowComponent } from '@shared/components/chats/chat-window/chat-window.component';
import { SupportChat } from '@features/chat/models/message.model';
import { AuthService } from '@core/services/auth.service';
import { ChatService } from '@shared/services/live-support/chat.service';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-admin-chats-page',
	standalone: true,
	imports: [CommonModule, ChatWindowComponent],
	templateUrl: './admin-chats-page.component.html',
	styleUrls: ['./admin-chats-page.component.css'],
})
export class AdminChatsPageComponent {
	private chatService = inject(ChatService);
	private authService = inject(AuthService);
	private popupService = inject(PopupsService);

	chats = this.chatService.chats;
	selectedChatIndex = signal<number>(-1);
	isLoading = signal<boolean>(false);

	selectChat(chatIndex: number) {
		this.selectedChatIndex.set(chatIndex);
	}

	closeSelectedChat() {
		this.selectedChatIndex.set(-1);
	}

	getChatTitle(chat: SupportChat): string {
		return `${chat.userEmail || 'User'} (${chat.userType || 'Unknown'})`;
	}

	getLastMessagePreview(chat: SupportChat): string {
		if (chat.messages.length === 0) return 'No messages yet';
		const content = chat.messages[chat.messages.length - 1].content;
		return content.length > 50 ? content.substring(0, 50) + '...' : content;
	}

	// getLastMessageTime(chat: SupportChat): string {
	// 	if (!chat.lastMessage) return '';
	// 	const date = new Date(chat.lastMessage.timestamp);
	// 	const hours = date.getHours().toString().padStart(2, '0');
	// 	const minutes = date.getMinutes().toString().padStart(2, '0');
	// 	return `${hours}:${minutes}`;
	// }
	protected userId() {
		return this.authService.user()?.id || -1;
	}

	get selectedChat() {
		if (this.selectedChatIndex() === -1) {
			return null;
		}
		return this.chats()[this.selectedChatIndex()];
	}

	protected onSend(content: string) {
		this.chatService.sendMessage(this.selectedChat?.id!, content).subscribe({
			next: (newMessage) => {
				// Message sent successfully, no need to do anything as the chat will update via WebSocket
				// this.chat.update((old) => ({
				// 	...old!,
				// 	messages: [...(old!.messages || []), newMessage],
				// }));
			},
			error: (error) => {
				this.popupService.error(
					'Error sending message',
					'Failed to send your message. Please try again later.',
				);
				console.error('[UserChatComponent] Error sending message:', error);
			},
		});
	}
}
