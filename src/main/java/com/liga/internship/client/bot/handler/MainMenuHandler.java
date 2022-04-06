package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.service.MainMenuService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.liga.internship.client.bot.BotState.HANDLER_LOGIN;
import static com.liga.internship.client.commons.TextMessage.MESSAGE_COMEBACK;
import static com.liga.internship.client.commons.TextMessage.MESSAGE_MAIN_MENU;

/**
 * Обработчик входящих Message и CallbackQuery сообщений телеграм бота, связанных с отображением главного меню.
 * Обработчик хранит состояние просматриваемых данных.
 */
@Slf4j
@Component
@AllArgsConstructor
public class MainMenuHandler implements InputMessageHandler, InputCallbackHandler {
    private final MainMenuService mainMenuService;
    private final UserDataCache userDataCache;

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
        log.debug(callbackQuery.getMessage().toString());
        long chatId = callbackQuery.getMessage().getChatId();
        long userId = callbackQuery.getFrom().getId();
        userDataCache.setUsersCurrentBotState(userId, HANDLER_LOGIN);
        return mainMenuService.getMainMenuMessage(chatId, MESSAGE_COMEBACK);
    }
}
