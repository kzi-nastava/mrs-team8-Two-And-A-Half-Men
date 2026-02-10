import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatWindowComponent } from '@shared/components/chats/chat-window/chat-window.component';
import { AuthService } from '@core/services/auth.service';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';
import { ChatService } from '@shared/services/live-support/chat.service';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-user-chat',
	standalone: true,
	imports: [CommonModule, ChatWindowComponent],
	templateUrl: './user-chat.component.html',
	styleUrls: ['./user-chat.component.css'],
})
export class UserChatComponent {
	private chatService = inject(ChatService);
	private authService = inject(AuthService);
	private popupService = inject(PopupsService);

	isLoading = signal<boolean>(false);
	isChatOpen = signal<boolean>(false);
	isMinimized = signal<boolean>(true);

	chat = this.chatService.myChat;

	onMinimize() {
		this.isMinimized.set(true);
		this.isChatOpen.set(false);
	}

	onClose() {
		this.isChatOpen.set(false);
	}

	openChat() {
		this.isChatOpen.set(true);
		this.isMinimized.set(false);
	}

	shouldBeVisible() {
		return this.authService.user() && this.authService.user()?.role != LoggedInUserRole.ADMIN;
	}

	protected onSend(content: string) {
		this.chatService.sendMessage(this.chat()?.id!, content).subscribe({
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

	protected userId() {
		return this.authService.user()?.id || -1;
	}
}
