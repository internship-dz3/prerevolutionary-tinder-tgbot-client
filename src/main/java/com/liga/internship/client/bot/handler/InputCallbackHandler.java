package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface InputCallbackHandler {
    BotState getHandlerName();

    PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery);
}
