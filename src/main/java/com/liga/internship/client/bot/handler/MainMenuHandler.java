package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.service.MainMenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.liga.internship.client.commons.TextMessage.MESSAGE_MAIN_MENU;
/**
 * Обработчик входящих Message и CallbackQuery сообщений телеграм бота, связанных с отображением главного меню.
 * Обработчик хранит состояние просматриваемых данных.
 */
@Component
@AllArgsConstructor
public class MainMenuHandler implements InputMessageHandler, InputCallbackHandler {
    private final MainMenuService mainMenuService;

    @Override
    public BotState getHandlerName() {
        return BotState.HANDLER_MAIN_MENU;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(), MESSAGE_MAIN_MENU);
    }

    @Override
    public PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery) {
        return handleMessage(callbackQuery.getMessage());
    }
}
