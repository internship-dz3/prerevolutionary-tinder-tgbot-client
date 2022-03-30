package com.liga.internship.client.bot;

import com.liga.internship.client.cache.UserDataCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@AllArgsConstructor
public class TelegramFacade {
    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;

    public BotApiMethod<?> handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return processCallBackQuery(callbackQuery);
        }
        Message message = update.getMessage();
        SendMessage sendMessage = null;
        if (message != null && message.hasText()) {
            sendMessage = handleInputMessage(message);
        }
        return sendMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();
        BotState botState;
        switch (inputMsg) {
            case "/start":
                botState = BotState.FILLING_PROFILE;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }
        userDataCache.setUsersCurrentBotState(userId, botState);
        return botStateContext.processInputMessage(botState, message);
    }

    private BotApiMethod<?> processCallBackQuery(CallbackQuery callbackQuery) {
        final long userId = callbackQuery.getFrom().getId();
        return botStateContext.processCallback(userDataCache.getUsersCurrentBotState(userId), callbackQuery);
    }
}
