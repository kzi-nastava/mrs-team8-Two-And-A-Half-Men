import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatWindowComponent } from '@features/chat/components/chat-window/chat-window.component';
import { SupportChatService } from '@features/chat/services/support-chat.service';
import { SupportChat } from '@features/chat/models/message.model';
import { AuthService } from '@core/services/auth.service';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

@Component({
	selector: 'app-user-chat',
	standalone: true,
	imports: [CommonModule, ChatWindowComponent],
	templateUrl: './user-chat.component.html',
	styleUrls: ['./user-chat.component.css'],
})
export class UserChatComponent implements OnInit {
	private chatService = inject(SupportChatService);
	private authService = inject(AuthService);

	chat = signal<SupportChat | null>(null);
	isLoading = signal<boolean>(false);
	isChatOpen = signal<boolean>(false);
	isMinimized = signal<boolean>(true);

	ngOnInit() {
		this.loadChat();
	}

	private loadChat() {
		this.isLoading.set(true);
		this.chatService.getMyChat().subscribe({
			next: (chat) => {
				this.chat.set(chat);
				this.isLoading.set(false);
			},
			error: (error) => {
				console.error('[UserChatPage] Error loading chat:', error);
				this.isLoading.set(false);
			},
		});
	}

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
}
