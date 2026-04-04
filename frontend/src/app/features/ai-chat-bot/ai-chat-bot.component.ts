import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TextareaModule } from 'primeng/textarea';
import { SplitterModule } from 'primeng/splitter';

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
  imports: [CommonModule, FormsModule, ButtonModule, TextareaModule, SplitterModule],
  templateUrl: './ai-chat-bot.component.html',
  styleUrl: './ai-chat-bot.component.scss',
})
export class AiChatBotComponent {
  sessions: Session[] = [
    { id: 1, title: 'Medical Report Analysis', lastActive: '5h' },
    { id: 2, title: 'Patient Consultation', lastActive: '2d' },
    { id: 3, title: 'Treatment Plan Review', lastActive: '1w' },
    { id: 4, title: 'Lab Results Summary', lastActive: '2w' },
    { id: 5, title: 'Prescription Inquiry', lastActive: '1mo' },
  ];

  messages: Message[] = [
    {
      role: 'user',
      content: 'Can you help me analyze this medical report?',
      timestamp: '10:30 AM',
    },
    {
      role: 'assistant',
      content:
        "Of course! I'd be happy to help you analyze the medical report. Please share the details or upload the document, and I'll provide a comprehensive breakdown of the findings.",
      timestamp: '10:30 AM',
    },
    {
      role: 'user',
      content: 'Here are the lab results: WBC 7.5, RBC 4.8, Hemoglobin 14.2',
      timestamp: '10:32 AM',
    },
    {
      role: 'assistant',
      content:
        'Based on the lab results you provided:\n\n• WBC (White Blood Cells): 7.5 x10³/µL — Within normal range (4.5-11.0)\n• RBC (Red Blood Cells): 4.8 x10⁶/µL — Within normal range (4.5-5.5)\n• Hemoglobin: 14.2 g/dL — Within normal range (12.0-17.5)\n\nAll values appear to be within normal limits. Is there anything specific you would like me to focus on?',
      timestamp: '10:32 AM',
    },
  ];

  userInput = '';

  sendMessage(): void {
    if (this.userInput.trim()) {
      this.messages.push({
        role: 'user',
        content: this.userInput.trim(),
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      });
      this.userInput = '';
    }
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }
}
