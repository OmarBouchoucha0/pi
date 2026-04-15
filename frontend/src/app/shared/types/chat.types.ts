export interface ChatMessage {
  id: number;
  sessionId: number;
  senderType: 'PATIENT' | 'AI';
  content: string;
  messageType: string;
  sentAt: string;
}

export interface ChatMessagePayload {
  sessionId: number;
  senderType: 'PATIENT' | 'AI';
  content: string;
  messageType: 'TEXT' | 'IMAGE' | 'SYSTEM';
}
