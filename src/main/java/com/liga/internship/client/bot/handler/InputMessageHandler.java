package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Обработчик входящих Message сообщений телеграм бота
 */
public interface InputMessageHandler {
    /**
     * @return имя-состояние обработчика
     */
    BotState getHandlerName();

    /**
     * Улавливание и обработка callBackQuery телеграм бота
     *
     * @param message - входящий callBackQuery
     * @return сообщение в виде SendMessage, SendPhoto, EditedMessageMedia
     */
    PartialBotApiMethod<?> handleMessage(Message message);
}
