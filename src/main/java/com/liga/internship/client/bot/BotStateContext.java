package com.liga.internship.client.bot;

import com.liga.internship.client.bot.handler.InputCallbackHandler;
import com.liga.internship.client.bot.handler.InputMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {
    private final Map<BotState, InputMessageHandler> inputHandlers = new HashMap<>();
    private final Map<BotState, InputCallbackHandler> callbackHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> inputMessageHandlers,
                           List<InputCallbackHandler> inputCallbackHandlers) {
        inputMessageHandlers.forEach(handler -> this.inputHandlers.put(handler.getHandlerName(), handler));
        inputCallbackHandlers.forEach(handler -> this.callbackHandlers.put(handler.getHandlerName(), handler));
    }

    public PartialBotApiMethod<?> processInputCallback(BotState currentState, CallbackQuery callbackQuery) {
        InputCallbackHandler currentInputCallbackHandler = findCallbackHandler(currentState);
        return currentInputCallbackHandler.handleCallback(callbackQuery);
    }

    private InputCallbackHandler findCallbackHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return callbackHandlers.get(BotState.FILLING_PROFILE_START);
        }
        if (isVotingState(currentState)) {
            return callbackHandlers.get(BotState.START_VOTING);
        }
        return callbackHandlers.get(currentState);
    }

    private boolean isVotingState(BotState currentState) {
        switch (currentState) {
            case START_VOTING:
            case CONTINUE_VOTING:
            case STOP_VOTING:
                return true;
            default:
                return false;
        }
    }

    private boolean isFillingProfileState(BotState currentState) {
        switch (currentState) {
            case FILLING_PROFILE_ASK_AGE:
            case FILLING_PROFILE_ASK_LOOK:
            case FILLING_PROFILE_ASK_GENDER:
            case FILLING_PROFILE_ASK_NAME:
            case FILLING_PROFILE_ASK_DESCRIBE:
            case FILLING_PROFILE_COMPLETE:
            case FILLING_PROFILE_START:
                return true;
            default:
                return false;
        }
    }

    public PartialBotApiMethod<?> processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentInputMessageHandler = findMessageHandler(currentState);
        return currentInputMessageHandler.handleMessage(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return inputHandlers.get(BotState.FILLING_PROFILE_START);
        }
        if (isVotingState(currentState)) {
            return inputHandlers.get(BotState.START_VOTING);
        }
        return inputHandlers.get(currentState);
    }
}
