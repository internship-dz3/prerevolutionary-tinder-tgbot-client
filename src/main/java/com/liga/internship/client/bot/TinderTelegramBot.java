package com.liga.internship.client.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

/**
 * Основной бин телеграм бота
 */
@Component
public class TinderTelegramBot extends TelegramLongPollingBot {
    private final TelegramFacade telegramFacade;

    @Value("${bot.name}")
    private String botUserName;

    @Value("${bot.token}")
    private String botToken;

    public TinderTelegramBot(TelegramFacade telegramFacade) {
        this.telegramFacade = telegramFacade;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public String getBotUsername() {
        return this.botUserName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            PartialBotApiMethod<?> replyMessage = telegramFacade.handleUpdate(update);
            if (replyMessage instanceof BotApiMethod) {
                execute((BotApiMethod<? extends Serializable>) replyMessage);
            } else if (replyMessage instanceof SendPhoto) {
                execute((SendPhoto) replyMessage);
            } else if (replyMessage instanceof EditMessageMedia) {
                execute((EditMessageMedia) replyMessage);
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
