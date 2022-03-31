package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputMessageHandler {
    BotState getHandlerName();

    PartialBotApiMethod<?> handleMessage(Message message);
}
