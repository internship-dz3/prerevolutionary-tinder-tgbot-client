package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputHandler {
    BotState getHandlerName();

    SendMessage handleCallback(CallbackQuery callback);

    SendMessage handleMessage(Message message);
}
