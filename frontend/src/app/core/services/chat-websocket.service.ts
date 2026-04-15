import { Injectable, OnDestroy } from '@angular/core';
import { RxStomp, RxStompConfig } from '@stomp/rx-stomp';
import { IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Observable, Subject, BehaviorSubject, Subscription } from 'rxjs';
import { ChatMessage, ChatMessagePayload } from '../../shared/types/chat.types';
import { RxStompState } from '@stomp/rx-stomp';

@Injectable({
  providedIn: 'root',
})
export class ChatWebSocketService implements OnDestroy {
  private rxStomp: RxStomp;
  private messageSubject = new Subject<ChatMessage>();
  private connectionStatusSubject = new BehaviorSubject<boolean>(false);
  private currentSessionId: number | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 10;
  private reconnectInterval = 5000;
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  private isManualDisconnect = false;
  private subscription: Subscription | null = null;
  private messageSubscription: Subscription | null = null;
  private stateSubscription: Subscription | null = null;

  messages$: Observable<ChatMessage>;
  connectionStatus$: Observable<boolean>;

  constructor() {
    this.rxStomp = new RxStomp();
    this.messages$ = this.messageSubject.asObservable();
    this.connectionStatus$ = this.connectionStatusSubject.asObservable();

    this.initConnection();
  }

  private initConnection(): void {
    const config: RxStompConfig = {
      webSocketFactory: () => new SockJS('http://localhost:8080/ws/chat'),
      reconnectDelay: this.reconnectInterval,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
    };

    this.rxStomp.configure(config);

    this.stateSubscription = this.rxStomp.connectionState$.subscribe((state) => {
      const isConnected = state === RxStompState.OPEN;
      this.connectionStatusSubject.next(isConnected);

      if (isConnected) {
        this.reconnectAttempts = 0;
        if (this.currentSessionId) {
          this.subscribeToSession(this.currentSessionId);
        }
      } else if (!this.isManualDisconnect && this.reconnectAttempts < this.maxReconnectAttempts) {
        this.scheduleReconnect();
      }
    });

    this.rxStomp.stompErrors$.subscribe((frame) => {
      console.error('STOMP error:', frame.headers['message']);
    });

    this.rxStomp.activate();
  }

  private scheduleReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('Max reconnection attempts reached');
      return;
    }

    this.reconnectAttempts++;
    console.log(
      `Scheduling reconnection attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts}`,
    );

    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
    }
    this.reconnectTimer = setTimeout(() => {
      if (!this.connectionStatusSubject.value) {
        this.rxStomp.activate();
      }
    }, this.reconnectInterval);
  }

  connect(): void {
    this.isManualDisconnect = false;
    if (!this.connectionStatusSubject.value) {
      this.rxStomp.activate();
    }
  }

  disconnect(): void {
    this.isManualDisconnect = true;
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
    }
    this.rxStomp.deactivate();
  }

  ngOnDestroy(): void {
    this.disconnect();
    if (this.messageSubscription) {
      this.messageSubscription.unsubscribe();
    }
    if (this.stateSubscription) {
      this.stateSubscription.unsubscribe();
    }
  }

  subscribeToSession(sessionId: number): void {
    if (this.currentSessionId === sessionId) return;

    if (this.messageSubscription) {
      this.messageSubscription.unsubscribe();
    }

    this.currentSessionId = sessionId;

    this.messageSubscription = this.rxStomp
      .watch({ destination: `/topic/chat/${sessionId}` })
      .subscribe({
        next: (frame: IMessage) => {
          try {
            const message: ChatMessage = JSON.parse(frame.body);
            this.messageSubject.next(message);
          } catch (e) {
            console.error('Failed to parse message:', e);
          }
        },
        error: (error) => {
          console.error('Subscription error:', error);
        },
      });
  }

  unsubscribeFromSession(sessionId: number): void {
    if (this.messageSubscription) {
      this.messageSubscription.unsubscribe();
      this.messageSubscription = null;
    }
    if (this.currentSessionId === sessionId) {
      this.currentSessionId = null;
    }
  }

  sendMessage(
    sessionId: number,
    content: string,
    messageType: 'TEXT' | 'IMAGE' | 'SYSTEM' = 'TEXT',
  ): void {
    const payload: ChatMessagePayload = {
      sessionId,
      senderType: 'PATIENT',
      content,
      messageType,
    };

    this.rxStomp.publish({
      destination: `/app/chat.sendMessage/${sessionId}`,
      body: JSON.stringify(payload),
    });
  }

  isConnected(): boolean {
    return this.connectionStatusSubject.value;
  }
}
