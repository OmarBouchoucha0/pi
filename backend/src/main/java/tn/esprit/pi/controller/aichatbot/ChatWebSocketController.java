package tn.esprit.pi.controller.aichatbot;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.pi.dto.aichatbot.ChatMessageCreateRequest;
import tn.esprit.pi.dto.aichatbot.ChatMessageResponse;
import tn.esprit.pi.enums.aichatbot.MessageType;
import tn.esprit.pi.enums.aichatbot.SenderType;
import tn.esprit.pi.service.aichatbot.AiChatService;
import tn.esprit.pi.service.aichatbot.ChatMessageService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final AiChatService aiChatService;

    @MessageMapping("/chat.sendMessage/{sessionId}")
    public ChatMessageResponse handleMessage(
            @DestinationVariable Long sessionId,
            ChatMessageCreateRequest request,
            Principal principal) {

        log.info("Received message for session {}: {}", sessionId, request.getContent());

        request.setSessionId(sessionId);
        ChatMessageResponse userMessage = chatMessageService.create(request);

        if (request.getSenderType() == SenderType.PATIENT) {
            if (aiChatService.isConfigured()) {
                try {
                    var conversationHistory = chatMessageService.findBySessionId(sessionId);
                    String aiResponse = aiChatService.chat(request.getContent(), conversationHistory);

                    ChatMessageCreateRequest aiRequest = new ChatMessageCreateRequest();
                    aiRequest.setSessionId(sessionId);
                    aiRequest.setSenderType(SenderType.AI);
                    aiRequest.setContent(aiResponse);
                    aiRequest.setMessageType(MessageType.TEXT);

                    ChatMessageResponse aiMessage = chatMessageService.create(aiRequest);
                    messagingTemplate.convertAndSend("/topic/chat/" + sessionId, aiMessage);

                } catch (Exception e) {
                    log.error("Error generating AI response: {}", e.getMessage(), e);
                    ChatMessageCreateRequest errorRequest = new ChatMessageCreateRequest();
                    errorRequest.setSessionId(sessionId);
                    errorRequest.setSenderType(SenderType.AI);
                    errorRequest.setContent("Sorry, I'm having trouble generating a response right now. Please try again.");
                    errorRequest.setMessageType(MessageType.TEXT);
                    ChatMessageResponse errorMessage = chatMessageService.create(errorRequest);
                    messagingTemplate.convertAndSend("/topic/chat/" + sessionId, errorMessage);
                }
            } else {
                ChatMessageCreateRequest unavailableRequest = new ChatMessageCreateRequest();
                unavailableRequest.setSessionId(sessionId);
                unavailableRequest.setSenderType(SenderType.AI);
                unavailableRequest.setContent("AI assistant is currently unavailable. Please contact the administrator to set up the AI service.");
                unavailableRequest.setMessageType(MessageType.TEXT);
                ChatMessageResponse unavailableMessage = chatMessageService.create(unavailableRequest);
                messagingTemplate.convertAndSend("/topic/chat/" + sessionId, unavailableMessage);
            }
        }

        return userMessage;
    }
}
