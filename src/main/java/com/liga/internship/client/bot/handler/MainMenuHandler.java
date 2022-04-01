package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.FavoritesDataCache;
import com.liga.internship.client.cache.TinderDataCache;
import com.liga.internship.client.service.MainMenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.liga.internship.client.commons.TextMessage.MESSAGE_MAIN_MENU;

@Component
@AllArgsConstructor
public class MainMenuHandler implements InputMessageHandler, InputCallbackHandler {
    private final MainMenuService mainMenuService;
    private TinderDataCache tinderDataCache;
    private FavoritesDataCache favoritesDataCache;

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        Long userId = message.getFrom().getId();
        tinderDataCache.removeProcessList(userId);
        favoritesDataCache.removeProcessList(userId);
        return mainMenuService.getMainMenuMessage(message.getChatId(), MESSAGE_MAIN_MENU);
    }

    @Override
    public PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery) {
        return handleMessage(callbackQuery.getMessage());
    }
}
