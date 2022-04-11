package com.liga.internship.client.bot;

import com.liga.internship.client.bot.handler.InputCallbackHandler;
import com.liga.internship.client.bot.handler.InputMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BotStateContext {
    private final Map<BotState, InputMessageHandler> inputHandlers = new EnumMap<>(BotState.class);
    private final Map<BotState, InputCallbackHandler> callbackHandlers = new EnumMap<>(BotState.class);

    public BotStateContext(List<InputMessageHandler> inputMessageHandlers,
                           List<InputCallbackHandler> inputCallbackHandlers) {
        inputMessageHandlers.forEach(handler -> this.inputHandlers.put(handler.getHandlerName(), handler));
        inputCallbackHandlers.forEach(handler -> this.callbackHandlers.put(handler.getHandlerName(), handler));
    }

    public PartialBotApiMethod<?> processInputCallback(BotState currentState, CallbackQuery callbackQuery) {
        InputCallbackHandler currentInputCallbackHandler = findCallbackHandler(currentState);
        log.debug("processInputCallback, botState: {}, callbackQuery {}, currentInputCallbackHandler {}", currentState.name(), callbackQuery, currentInputCallbackHandler.getHandlerName());
        return currentInputCallbackHandler.handleCallback(callbackQuery);
    }

    public PartialBotApiMethod<?> processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentInputMessageHandler = findMessageHandler(currentState);
        log.debug("processInputCallback, botState: {}, callbackQuery {}, currentInputMessageHandler {}", currentState.name(), message, currentInputMessageHandler.getHandlerName());
        return currentInputMessageHandler.handleMessage(message);
    }

    private InputCallbackHandler findCallbackHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return callbackHandlers.get(BotState.HANDLER_PROFILE_FILLING);
        }
        if (isVotingState(currentState)) {
            return callbackHandlers.get(BotState.HANDLER_TINDER);
        }
        if (isShowFavoritesState(currentState)) {
            return callbackHandlers.get(BotState.HANDLER_SHOW_FAVORITES);
        }
        return callbackHandlers.get(currentState);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return inputHandlers.get(BotState.HANDLER_PROFILE_FILLING);
        }
        if (isVotingState(currentState)) {
            return inputHandlers.get(BotState.HANDLER_TINDER);
        }
        if (isShowFavoritesState(currentState)) {
            return inputHandlers.get(BotState.HANDLER_SHOW_FAVORITES);
        }
        return inputHandlers.get(currentState);
    }

    private boolean isFillingProfileState(BotState currentState) {
        switch (currentState) {
            case FILLING_PROFILE_ASK_AGE:
            case FILLING_PROFILE_ASK_LOOK:
            case FILLING_PROFILE_ASK_GENDER:
            case FILLING_PROFILE_ASK_NAME:
            case FILLING_PROFILE_ASK_DESCRIBE:
            case FILLING_PROFILE_COMPLETE:
            case HANDLER_PROFILE_FILLING:
                return true;
            default:
                return false;
        }
    }

    private boolean isShowFavoritesState(BotState currentState) {
        switch (currentState) {
            case SHOW_NEXT_FAVORITE:
            case SHOW_PREV_FAVORITE:
            case HANDLER_SHOW_FAVORITES:
                return true;
            default:
                return false;
        }
    }

    private boolean isVotingState(BotState currentState) {
        switch (currentState) {
            case HANDLER_TINDER:
            case CONTINUE_VOTING:
            case STOP_VOTING:
                return true;
            default:
                return false;
        }
    }
}
