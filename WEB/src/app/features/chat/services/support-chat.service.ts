import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Message, SupportChat, SendMessageRequest } from '@features/chat/models/message.model';

@Injectable({
	providedIn: 'root',
})
export class SupportChatService {
	private readonly API_URL = '/api/v1/support';

	constructor(private http: HttpClient) {}

	getMyChat(): Observable<SupportChat> {
		return this.http
			.get<SupportChat>(`${this.API_URL}/my-chat`)
			.pipe(map((chat) => this.convertChatDates(chat)));
	}
	getAllActiveChats(): Observable<SupportChat[]> {
		return this.http
			.get<SupportChat[]>(`${this.API_URL}/chats`)
			.pipe(map((chats) => chats.map((chat) => this.convertChatDates(chat))));
	}

	getChatById(chatId: number): Observable<SupportChat> {
		return this.http
			.get<SupportChat>(`${this.API_URL}/chats/${chatId}`)
			.pipe(map((chat) => this.convertChatDates(chat)));
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

	private convertChatDates(chat: SupportChat): SupportChat {
		return {
			...chat,
			createdAt: new Date(chat.createdAt),
			closedAt: chat.closedAt ? new Date(chat.closedAt) : undefined,
			lastMessage: chat.lastMessage ? this.convertMessageDates(chat.lastMessage) : undefined,
		};
	}
}
