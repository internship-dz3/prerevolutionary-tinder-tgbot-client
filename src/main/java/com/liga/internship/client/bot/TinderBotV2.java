package com.liga.internship.client.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TinderBotV2 extends TelegramLongPollingBot {
    private final TelegramFacade telegramFacade;

    @Value("${bot.name}")
    private String botUserName;

    @Value("${bot.token}")
    private String botToken;

    public TinderBotV2(TelegramFacade telegramFacade) {
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
            BotApiMethod<?> botApiMethod = telegramFacade.handleUpdate(update);
            execute(botApiMethod);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
