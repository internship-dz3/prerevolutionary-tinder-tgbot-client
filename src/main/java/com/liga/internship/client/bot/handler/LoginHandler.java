package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.commons.ButtonCallback;
import com.liga.internship.client.commons.TextMessage;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.service.MainMenuService;
import com.liga.internship.client.service.ProfileService;
import com.liga.internship.client.service.V1RestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

import static com.liga.internship.client.bot.BotState.HANDLER_LOGIN;

/**
 * Обработчик входящих Message и CallbackQuery сообщений телеграм бота, связанных с логином пользователя.
 * Если пользователя нет на сервере, создается новый пользователь.
 * Обработчик хранит состояние просматриваемых данных.
 */
@Component
@AllArgsConstructor
public class LoginHandler implements InputMessageHandler, InputCallbackHandler {
    private final V1RestService v1RestService;
    private final UserDataCache userDataCache;
    private final ProfileService profileService;
    private final MainMenuService mainMenuService;

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
        Optional<UserProfile> userProfile = v1RestService.userLogin(userId);
        if (userProfile.isPresent()) {
            userDataCache.setUsersCurrentBotState(userId, BotState.HANDLER_MAIN_MENU);
            userDataCache.saveUserProfile(userId, userProfile.get());
            return mainMenuService.getMainMenuMessage(chatId, TextMessage.MESSAGE_MAIN_MENU);
        }
        userDataCache.setUsersCurrentBotState(userId, BotState.HANDLER_PROFILE_FILLING);
        return profileService.getChooseButtons(chatId, TextMessage.MESSAGE_WELCOME, ButtonCallback.FILL_FORM);
    }

    @Override
    public PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery) {
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        return getSendMessage(userId, chatId);
    }
}
