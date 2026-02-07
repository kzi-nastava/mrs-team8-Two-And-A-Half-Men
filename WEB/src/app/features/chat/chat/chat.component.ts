import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Message } from '@features/chat/models/message.model'

@Component({
	selector: 'app-chat',
	standalone: true,
	imports: [CommonModule, FormsModule],
	templateUrl: './chat.component.html',
	styleUrls: ['./chat.component.css'],
})
export class ChatComponent implements OnInit {
	@Input() title: string = 'Live support';
	@Input() messages: Message[] = [];
	@Input() currentUserId: number = 0;
	@Input() isOpen: boolean = true;

	@Output() isOpenChange = new EventEmitter<boolean>();
	@Output() sendMessage = new EventEmitter<string>();
	@Output() minimize = new EventEmitter<void>();
	@Output() close = new EventEmitter<void>();

	newMessage: string = '';

	ngOnInit() {
		this.scrollToBottom();
	}

	ngOnChanges() {
		if (this.messages.length > 0) {
			setTimeout(() => this.scrollToBottom(), 100);
		}
	}

	onSend() {
		if (this.newMessage.trim()) {
			this.sendMessage.emit(this.newMessage.trim());
			this.newMessage = '';
			setTimeout(() => this.scrollToBottom(), 100);
		}
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
		return message.senderId === this.currentUserId;
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
}
