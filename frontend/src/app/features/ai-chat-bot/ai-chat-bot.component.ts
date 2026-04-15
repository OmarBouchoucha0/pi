import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TextareaModule } from 'primeng/textarea';
import { SplitterModule } from 'primeng/splitter';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { ChatService, ChatMessage as HttpChatMessage } from '../../core/services/chat.service';
import { ChatWebSocketService } from '../../core/services/chat-websocket.service';
import { ChatMessage as WsChatMessage } from '../../shared/types/chat.types';
import { AuthService } from '../../core/services/auth.service';

interface Message {
  role: 'user' | 'assistant';
  content: string;
  timestamp: string;
}

interface Session {
  id: number;
  title: string;
  lastActive: string;
}

@Component({
  selector: 'app-ai-chat-bot',
  imports: [CommonModule, FormsModule, ButtonModule, TextareaModule, SplitterModule, ToastModule],
  providers: [MessageService],
  templateUrl: './ai-chat-bot.component.html',
  styleUrl: './ai-chat-bot.component.scss',
})
export class AiChatBotComponent implements OnInit, OnDestroy {
  private chatService = inject(ChatService);
  private wsService = inject(ChatWebSocketService);
  private authService = inject(AuthService);
  private messageService = inject(MessageService);

  sessions: Session[] = [];
  messages: Message[] = [];
  userInput = '';
  selectedSessionId: number | null = null;
  loading = false;
  sendingMessage = false;
  isTypingIndicator = false;

  private currentUserId: number | null = null;
  private tenantId = 1;
  private wsSubscription: Subscription | null = null;

  ngOnInit(): void {
    const user = this.authService.getStoredUser();
    if (user) {
      this.currentUserId = user.id;
      this.loadSessions();
    }
  }

  ngOnDestroy(): void {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
    if (this.selectedSessionId) {
      this.wsService.unsubscribeFromSession(this.selectedSessionId);
    }
  }

  loadSessions(): void {
    if (!this.currentUserId) return;

    this.loading = true;
    this.chatService.getSessionsByPatient(this.currentUserId).subscribe({
      next: (sessions) => {
        this.sessions = sessions.map((s) => ({
          id: s.id,
          title: s.name,
          lastActive: this.formatDate(s.startedAt),
        }));
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 60) return `${diffMins}m`;
    if (diffHours < 24) return `${diffHours}h`;
    if (diffDays < 7) return `${diffDays}d`;
    return date.toLocaleDateString();
  }

  createSession(): void {
    if (!this.currentUserId) return;

    this.chatService
      .createSession('New Conversation', this.tenantId, this.currentUserId)
      .subscribe({
        next: (session) => {
          this.sessions.unshift({
            id: session.id,
            title: session.name,
            lastActive: 'now',
          });
          this.selectSession(session.id);
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to create session',
          });
        },
      });
  }

  deleteSession(sessionId: number, event: Event): void {
    event.stopPropagation();

    this.chatService.deleteSession(sessionId).subscribe({
      next: () => {
        this.sessions = this.sessions.filter((s) => s.id !== sessionId);
        if (this.selectedSessionId === sessionId) {
          this.wsService.unsubscribeFromSession(sessionId);
          this.selectedSessionId = null;
          this.messages = [];
        }
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to delete session',
        });
      },
    });
  }

  selectSession(sessionId: number): void {
    if (this.selectedSessionId === sessionId) return;

    if (this.selectedSessionId) {
      this.wsService.unsubscribeFromSession(this.selectedSessionId);
    }

    this.selectedSessionId = sessionId;
    this.loadMessages(sessionId);
    this.wsService.subscribeToSession(sessionId);
    this.subscribeToWebSocketMessages();
  }

  private subscribeToWebSocketMessages(): void {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }

    this.wsSubscription = this.wsService.messages$.subscribe({
      next: (msg: WsChatMessage) => {
        if (msg.sessionId === this.selectedSessionId) {
          this.messages.push({
            role: msg.senderType === 'PATIENT' ? 'user' : 'assistant',
            content: msg.content,
            timestamp: this.formatTime(msg.sentAt),
          });
          this.isTypingIndicator = false;
          this.sendingMessage = false;
        }
      },
      error: (error: Error) => {
        console.error('WebSocket message error:', error);
      },
    });
  }

  loadMessages(sessionId: number): void {
    this.chatService.getMessages(sessionId).subscribe({
      next: (msgs: HttpChatMessage[]) => {
        this.messages = msgs.map((m) => ({
          role: m.senderType === 'PATIENT' ? 'user' : 'assistant',
          content: m.content,
          timestamp: this.formatTime(m.sentAt),
        }));
      },
      error: () => {
        this.messages = [];
      },
    });
  }

  formatTime(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  sendMessage(): void {
    if (!this.userInput.trim() || !this.selectedSessionId || this.sendingMessage) return;

    const userMessage = this.userInput.trim();
    this.userInput = '';
    this.sendingMessage = true;
    this.isTypingIndicator = true;

    this.wsService.sendMessage(this.selectedSessionId!, userMessage, 'TEXT');

    this.messages.push({
      role: 'user',
      content: userMessage,
      timestamp: this.formatTime(new Date().toISOString()),
    });
  }

  onTypingComplete(): void {
    this.sendingMessage = false;
    this.isTypingIndicator = false;
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }
}
