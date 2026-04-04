import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AiChatBotComponent } from './ai-chat-bot.component';

describe('AiChatBotComponent', () => {
  let component: AiChatBotComponent;
  let fixture: ComponentFixture<AiChatBotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AiChatBotComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AiChatBotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have sessions initialized', () => {
    expect(component.sessions.length).toBeGreaterThan(0);
  });

  it('should have messages initialized', () => {
    expect(component.messages.length).toBeGreaterThan(0);
  });

  it('should have empty userInput initially', () => {
    expect(component.userInput).toBe('');
  });

  it('should add a user message when sendMessage is called with non-empty input', () => {
    component.userInput = 'Hello';
    component.sendMessage();
    expect(component.messages.length).toBe(5);
    expect(component.messages[component.messages.length - 1].role).toBe('user');
    expect(component.messages[component.messages.length - 1].content).toBe('Hello');
  });

  it('should clear userInput after sending a message', () => {
    component.userInput = 'Hello';
    component.sendMessage();
    expect(component.userInput).toBe('');
  });

  it('should not add a message when userInput is empty', () => {
    const initialLength = component.messages.length;
    component.userInput = '';
    component.sendMessage();
    expect(component.messages.length).toBe(initialLength);
  });

  it('should not add a message when userInput is only whitespace', () => {
    const initialLength = component.messages.length;
    component.userInput = '   ';
    component.sendMessage();
    expect(component.messages.length).toBe(initialLength);
  });

  it('should call sendMessage on Enter key without Shift', () => {
    spyOn(component, 'sendMessage');
    component.onKeydown(new KeyboardEvent('keydown', { key: 'Enter' }) as KeyboardEvent);
    expect(component.sendMessage).toHaveBeenCalled();
  });

  it('should not call sendMessage on Enter key with Shift', () => {
    spyOn(component, 'sendMessage');
    component.onKeydown(
      new KeyboardEvent('keydown', { key: 'Enter', shiftKey: true }) as KeyboardEvent,
    );
    expect(component.sendMessage).not.toHaveBeenCalled();
  });
});
