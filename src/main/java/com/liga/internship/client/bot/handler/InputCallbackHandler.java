package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Обработчик входящих CallbackQuery сообщений телеграм бота
 */
public interface InputCallbackHandler {
    /**
     * @return имя-состояние обработчика
     */
    BotState getHandlerName();

    /**
     * Улавливание и обработка callBackQuery телеграм бота
     *
     * @param callbackQuery - входящий callBackQuery
     * @return сообщение в виде SendMessage, SendPhoto, EditedMessageMedia
     */
    PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery);
}
