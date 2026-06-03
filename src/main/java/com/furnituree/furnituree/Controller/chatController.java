package com.furnituree.furnituree.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.furnituree.furnituree.model.chatMessage;
import com.furnituree.furnituree.service.ChatService;

@Controller
@CrossOrigin(origins = "*")
public class chatController {

    private final ChatService chatService;

    public chatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public chatMessage sendMessage(chatMessage message) {
        return chatService.sendMessage(message);
    }

    @CrossOrigin(origins = "*")
    @MessageMapping("/chat.join")
    @SendTo("/topic/public")
    public chatMessage addUser(chatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        return chatService.addUser(message, headerAccessor);
    }
}
