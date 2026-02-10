export interface Message {
	id: number;
	chatId: number;
	senderId: number;
	content: string;
	senderType: 'DRIVER' | 'CUSTOMER' | 'ADMIN';
	adminRead: boolean;
	userRead: boolean;
	timestamp: Date;
}

export interface SupportChat {
	id: number;
	userId: number;
	userEmail?: string;
	userType?: string;
	status: 'ACTIVE' | 'CLOSED';
	messages: Message[];
}

export interface SendMessageRequest {
	content: string;
}
