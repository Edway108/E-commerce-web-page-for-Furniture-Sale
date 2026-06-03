package com.furnituree.furnituree.service;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import com.furnituree.furnituree.model.chatMessage;

@Service
public class ChatService {

    public chatMessage sendMessage(chatMessage message) {
        return message;
    }

    public chatMessage addUser(chatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", message.getSender());
        }
        return message;
    }
}
