package com.furnituree.furnituree.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.furnituree.furnituree.model.chatMessage;

@Controller
@CrossOrigin(origins = "*")
public class chatController {

    // to send a message
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public chatMessage sendMessage(chatMessage message) {
        return message;
    }

    // to tell the user JOIN
    @CrossOrigin(origins = "*")
    @MessageMapping("/chat.join")
    @SendTo("/topic/public")
    public chatMessage addUser(chatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", message.getSender());
        }
        return message;
    }

}
