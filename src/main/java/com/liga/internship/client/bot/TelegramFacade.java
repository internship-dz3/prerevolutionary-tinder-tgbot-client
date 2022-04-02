package com.liga.internship.client.bot;

import com.liga.internship.client.cache.UserDataCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.liga.internship.client.bot.BotState.*;
import static com.liga.internship.client.commons.ButtonInput.*;

@Slf4j
@Component
@AllArgsConstructor
public class TelegramFacade {
    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;

    public PartialBotApiMethod<?> handleUpdate(Update update) {
        Message message = update.getMessage();
        PartialBotApiMethod<?> sendMessage = null;
        if (message != null && message.hasText()) {
            sendMessage = handleInputMessage(message);
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            sendMessage = handleCallbackQuery(callbackQuery);
        }
        return sendMessage;
    }

    private PartialBotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
        long userId = callbackQuery.getFrom().getId();
        return botStateContext.processInputCallback(userDataCache.getUsersCurrentBotState(userId), callbackQuery);
    }

    private PartialBotApiMethod<?> handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();
        BotState botState;
        switch (inputMsg) {
            case START:
                botState = HANDLER_LOGIN;
                break;
            case CHANGE_PROFILE:
                botState = HANDLER_PROFILE_FILLING;
                break;
            case SEARCH:
                botState = HANDLER_TINDER;
                break;
            case USERFORM:
            case SHOW_PROFILE:
                botState = SHOW_USER_PROFILE;
                break;
            case FAVORITES:
            case FAVORITE:
            case ADMIRER:
            case LOVE:
                botState = HANDLER_SHOW_FAVORITES;
                break;
            case MAIN_MENU:
                botState = HANDLER_MAIN_MENU;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }
        userDataCache.setUsersCurrentBotState(userId, botState);
        return botStateContext.processInputMessage(botState, message);
    }
}
