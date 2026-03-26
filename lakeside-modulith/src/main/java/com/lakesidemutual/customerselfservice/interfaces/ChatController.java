package com.lakesidemutual.customerselfservice.interfaces;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/messages")
    @SendTo("/topic/messages")
    public ChatMessage onMessage(ChatMessage msg) {
        System.out.println("Chat message received: " + msg.content);
        return msg;
    }

    public static class ChatMessage {
        public String username;
        public String content;
        public String customerId;
        public boolean sentByOperator;
    }
}
