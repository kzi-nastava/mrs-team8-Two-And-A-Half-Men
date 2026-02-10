import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Message, SupportChat } from '@features/chat/models/message.model';

@Component({
	selector: 'app-chat-window',
	standalone: true,
	imports: [CommonModule, FormsModule],
	templateUrl: './chat-window.component.html',
	styleUrls: ['./chat-window.component.css'],
})
export class ChatWindowComponent {
	@Input() chat!: SupportChat;
	@Input() title: string = 'Live support';
	@Input() isOpen: boolean = true;
	@Input() isMinimizable: boolean = false;
	@Input() userId: number = -1;

	@Output() isOpenChange = new EventEmitter<boolean>();
	@Output() minimize = new EventEmitter<void>();
	@Output() close = new EventEmitter<void>();
	@Output() messageSend = new EventEmitter<string>();

	newMessage = signal<string>('');

	onSend() {
		const content = this.newMessage().trim();
		if (!content) return;
		this.messageSend.emit(content);
		this.newMessage.set('');
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
		return message.senderId === this.userId;
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

	protected isRead(message: Message) {
		if (!this.isOwnMessage(message)) {
			return false;
		}
		switch (message.senderType) {
			case 'ADMIN':
				return message.userRead;
			case 'DRIVER':
			case 'CUSTOMER':
				return message.adminRead;
			default:
				return false;
		}
	}
}
