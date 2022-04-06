package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.service.LoginService;
import com.liga.internship.client.service.MainMenuService;
import com.liga.internship.client.service.V1RestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

import static com.liga.internship.client.bot.BotState.*;
import static com.liga.internship.client.commons.TextMessage.MESSAGE_MAIN_MENU;
import static com.liga.internship.client.commons.TextMessage.MESSAGE_WELCOME;

/**
 * Обработчик входящих Message и CallbackQuery сообщений телеграм бота, связанных с логином пользователя.
 * Если пользователя нет на сервере, создается новый пользователь.
 * Обработчик хранит состояние просматриваемых данных.
 */
@Slf4j
@Component
@AllArgsConstructor
public class LoginHandler implements InputMessageHandler, InputCallbackHandler {
    private final V1RestService v1RestService;
    private final UserDataCache userDataCache;
    private final MainMenuService mainMenuService;
    private final LoginService loginService;

    @Override
    public BotState getHandlerName() {
        return HANDLER_LOGIN;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        return getSendMessage(userId, chatId);
    }

    private SendMessage getSendMessage(long userId, long chatId) {
        log.debug("UserID: " + userId +  "Chat ID: " + chatId + " :getSendMessage");
        Optional<UserProfile> userProfile = v1RestService.userLogin(userId);
        if (userProfile.isPresent()) {
            userDataCache.saveUserProfile(userId, userProfile.get());
            userDataCache.setLoginUser(userId, true);
            userDataCache.setUsersCurrentBotState(userId, HANDLER_MAIN_MENU);
            return mainMenuService.getMainMenuMessage(chatId, MESSAGE_MAIN_MENU);
        } else {
            userDataCache.setUsersCurrentBotState(userId, HANDLER_PROFILE_FILLING);
            return loginService.getMEssageWithFillFormMenu(chatId, MESSAGE_WELCOME);
        }
    }

    @Override
    public PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery) {
        log.debug(callbackQuery.getMessage().toString() + " handleCallback");
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        return getSendMessage(userId, chatId);
    }
}
