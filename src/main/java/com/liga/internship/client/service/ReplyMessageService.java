package com.liga.internship.client.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class ReplyMessageService {
    public SendMessage getReplyMessage(Long chatId, String replyMessage) {
        return new SendMessage(chatId.toString(), replyMessage);
    }
}
