package com.liga.internship.client.bot;

import com.liga.internship.client.bot.handler.InputHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {
    private final Map<BotState, InputHandler> inputHandlers = new HashMap<>();

    public BotStateContext(List<InputHandler> inputHandlers) {
        inputHandlers.forEach(handler -> this.inputHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processCallback(BotState currentState, CallbackQuery callback) {
        InputHandler currentInputHandler = findHandler(currentState);
        return currentInputHandler.handleCallback(callback);
    }

    private InputHandler findHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return inputHandlers.get(BotState.FILLING_PROFILE);
        }
        return inputHandlers.get(currentState);
    }

    private boolean isFillingProfileState(BotState currentState) {
        switch (currentState) {
            case ASK_AGE:
            case ASK_LOOK:
            case ASK_GENDER:
            case ASK_NAME:
            case ASK_DESCRIBE:
            case PROFILE_FILLED:
            case FILLING_PROFILE:
                return true;
            default:
                return false;
        }
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputHandler currentInputHandler = findHandler(currentState);
        return currentInputHandler.handleMessage(message);
    }
}
