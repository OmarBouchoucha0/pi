import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_BASE = 'http://localhost:8080';

export interface ChatSession {
  id: number;
  name: string;
  status: string;
  patientId: number;
  tenantId: number;
  startedAt: string;
  endedAt?: string;
}

export interface ChatMessage {
  id: number;
  sessionId: number;
  senderType: 'PATIENT' | 'AI';
  content: string;
  messageType: string;
  replyTo?: number;
  sentAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private http = inject(HttpClient);

  getSessionsByPatient(patientId: number): Observable<ChatSession[]> {
    return this.http.get<ChatSession[]>(`${API_BASE}/api/ai/chat-sessions/patient/${patientId}`);
  }

  createSession(name: string, tenantId: number, patientId: number): Observable<ChatSession> {
    return this.http.post<ChatSession>(`${API_BASE}/api/ai/chat-sessions`, {
      name,
      tenantId,
      patientId,
    });
  }

  deleteSession(sessionId: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE}/api/ai/chat-sessions/${sessionId}`);
  }

  getMessages(sessionId: number): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${API_BASE}/api/ai/chat-messages/session/${sessionId}`);
  }

  sendMessage(sessionId: number, content: string): Observable<ChatMessage> {
    return this.http.post<ChatMessage>(`${API_BASE}/api/ai/chat-messages`, {
      sessionId,
      content,
      senderType: 'PATIENT',
      messageType: 'TEXT',
    });
  }
}
