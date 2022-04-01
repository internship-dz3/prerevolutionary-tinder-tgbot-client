package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.FavoritesDataCache;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.service.FavoritesService;
import com.liga.internship.client.service.ImageCreatorService;
import com.liga.internship.client.service.MainMenuService;
import com.liga.internship.client.service.V1RestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.liga.internship.client.bot.BotState.*;
import static com.liga.internship.client.commons.Button.*;
import static com.liga.internship.client.commons.TextInput.*;
import static com.liga.internship.client.commons.TextMessage.*;

@Service
@AllArgsConstructor
public class FavoritesHandler implements InputCallbackHandler, InputMessageHandler {
    private final V1RestService v1RestService;
    private final UserDataCache dataCache;
    private final FavoritesService favoritesService;
    private final ImageCreatorService imageCreatorService;
    private final FavoritesDataCache favoritesDataCache;
    private final MainMenuService mainMenuService;

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_FAVORITES;
    }

    @Override
    public PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery) {
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        String callbackData = callbackQuery.getData();
        PartialBotApiMethod<?> replyMessage = null;

        if (callbackData.equals(CALLBACK_NEXT)) {
            UserProfile nextUser = favoritesDataCache.getNextUser(userId);
            if (dataCache.getUsersCurrentBotState(userId) == SHOW_PREV_FAVORITE) {
                nextUser = favoritesDataCache.getNextUser(userId);
            }
            File imageWithTextFile = imageCreatorService.getImageWithTextFile(nextUser, userId);
            replyMessage = favoritesService.getNextPrevInlineKeyboardEditedMessage(chatId, messageId, imageWithTextFile, nextUser.getUsername() + "Любим не любим");
            dataCache.setUsersCurrentBotState(userId, SHOW_NEXT_FAVORITE);
        }
        if (callbackData.equals(CALLBACK_PREV)) {
            UserProfile prevUser = favoritesDataCache.getPrevious(userId);
            if (dataCache.getUsersCurrentBotState(userId) == SHOW_NEXT_FAVORITE) {
                prevUser = favoritesDataCache.getPrevious(userId);
            }
            File imageWithTextFile = imageCreatorService.getImageWithTextFile(prevUser, userId);
            replyMessage = favoritesService.getNextPrevInlineKeyboardEditedMessage(chatId, messageId, imageWithTextFile, prevUser.getUsername() + "Любим не любим");
            dataCache.setUsersCurrentBotState(userId, SHOW_PREV_FAVORITE);
        }
        if (callbackData.equals(CALLBACK_MENU)) {
            favoritesDataCache.removeProcessList(userId);
            replyMessage = mainMenuService.getMainMenuMessage(chatId, MESSAGE_MAIN_MENU);
            dataCache.setUsersCurrentBotState(userId, SHOW_MAIN_MENU);
        }
        return replyMessage;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        String userButtonInput = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        long currentUserId = dataCache.getUserProfile(userId).getId();
        PartialBotApiMethod<?> replyMessage;
        File imageWithTextFile;
        List<UserProfile> favoritesList = getFavoriteList(userButtonInput, currentUserId);

        if (favoritesList.size() == 1) {
            UserProfile userProfile = favoritesList.get(0);
            imageWithTextFile = imageCreatorService.getImageWithTextFile(userProfile, userId);
            replyMessage = favoritesService.getMenuInlineKeyBoardService(chatId, imageWithTextFile, userProfile.getUsername());
        } else if (favoritesList.isEmpty()) {
            replyMessage = favoritesService.getReplyKeyboardTextMessage(chatId, MESSAGE_EMPTY);
        } else {
            favoritesDataCache.setProcessDataList(userId, favoritesList);
            UserProfile showFirst = favoritesDataCache.getNextUser(userId);
            imageWithTextFile = imageCreatorService.getImageWithTextFile(showFirst, userId);
            replyMessage = favoritesService.getNextPrevInlineKeyboardPhotoMessage(chatId, imageWithTextFile, showFirst.getUsername());
        }
        if (userButtonInput.equals(FAVORITES)) {
            replyMessage = favoritesService.getReplyKeyboardTextMessage(chatId, MESSAGE_FAVORITE);
            dataCache.setUsersCurrentBotState(userId, SHOW_USER_FAVORITES);
        }
        return replyMessage;
    }

    private List<UserProfile> getFavoriteList(String userButtonInput, long currentUserId) {
        switch (userButtonInput) {
            case FAVORITE:
                return v1RestService.getFavoritesList(currentUserId);
            case ADMIRER:
                return v1RestService.getAdmirerList(currentUserId);
            case LOVE:
                return v1RestService.getLoveList(currentUserId);
            default:
                return new ArrayList<>();
        }
    }
}
