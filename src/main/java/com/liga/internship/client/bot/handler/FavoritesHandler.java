package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.FavoritesDataCache;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.liga.internship.client.bot.BotState.*;
import static com.liga.internship.client.commons.ButtonCallback.*;
import static com.liga.internship.client.commons.ButtonInput.*;
import static com.liga.internship.client.commons.TextMessage.*;

/**
 * Обработчик входящих Message и CallbackQuery сообщений телеграм бота, связанных с просмотром страниц Любимцев.
 * Обработчик хранит состояние просматриваемых данных.
 */
@Slf4j
@Service
@AllArgsConstructor
public class FavoritesHandler implements InputCallbackHandler, InputMessageHandler {
    private final UserDataCache userDataCache;
    private final FavoritesDataCache favoritesDataCache;
    private final V1RestService v1RestService;
    private final FavoritesService favoritesService;
    private final ImageCreatorService imageCreatorService;
    private final TextService textService;

    @Override
    public BotState getHandlerName() {
        return BotState.HANDLER_SHOW_FAVORITES;
    }

    @Override
    public PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery) {
        log.debug(callbackQuery.getMessage().toString());
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        String callbackData = callbackQuery.getData();
        PartialBotApiMethod<?> replyMessage = null;
        UserProfile nextUser;
        File imageWithTextFile;
        String messageCaption;
        if (callbackData.equals(CALLBACK_NEXT)) {
            nextUser = favoritesDataCache.getNextUser(userId);
            if (userDataCache.getUsersCurrentBotState(userId) == SHOW_PREV_FAVORITE) {
                nextUser = favoritesDataCache.getNextUser(userId);
            }
            messageCaption = createMessageCation(userId, nextUser);
            imageWithTextFile = imageCreatorService.getImageWithTextFile(nextUser.getDescription(), userId);
            replyMessage = favoritesService.getNextPrevInlineKeyboardEditedMessage(chatId, messageId, imageWithTextFile, messageCaption);
            userDataCache.setUsersCurrentBotState(userId, SHOW_NEXT_FAVORITE);
        }
        if (callbackData.equals(CALLBACK_PREV)) {
            nextUser = favoritesDataCache.getPrevious(userId);
            if (userDataCache.getUsersCurrentBotState(userId) == SHOW_NEXT_FAVORITE) {
                nextUser = favoritesDataCache.getPrevious(userId);
            }
            messageCaption = createMessageCation(userId, nextUser);
            imageWithTextFile = imageCreatorService.getImageWithTextFile(nextUser.getDescription(), userId);
            replyMessage = favoritesService.getNextPrevInlineKeyboardEditedMessage(chatId, messageId, imageWithTextFile, messageCaption);
            userDataCache.setUsersCurrentBotState(userId, SHOW_PREV_FAVORITE);
        }
        if (callbackData.equals(CALLBACK_MENU)) {
            favoritesDataCache.removeFaforites(userId);
            userDataCache.setUsersCurrentBotState(userId, HANDLER_MAIN_MENU);
            replyMessage = favoritesService.getMainMenuMessage(chatId, MESSAGE_MAIN_MENU);
        }
        return replyMessage;
    }

    private String createMessageCation(long userId, UserProfile nextUser) {
        log.debug(nextUser.getUsername());
        String gender = MALE;
        String status = favoritesDataCache.getFaforiteListStatus(userId);
        if (nextUser.getGender().equals(CALLBACK_FEMALE)) {
            gender = FEMALE;
            if (status.equals(CAPTION_FAVORITE)) {
                status = CAPTION_FAVORITE_FEMALE;
            }
        }
        String transformedUsername = textService.translateTextIntoSlavOld(nextUser.getUsername());
        return String.format("%s, %s, %s", gender, transformedUsername, status);
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        log.debug(message.getChatId().toString());
        String userButtonInput = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        List<UserProfile> favoritesList = getFavoriteList(userButtonInput, userId);
        File imageWithTextFile;
        String messageCaption;
        PartialBotApiMethod<?> replyMessage;
        if (favoritesList.size() == 1) {
            UserProfile userProfile = favoritesList.get(0);
            messageCaption = createMessageCation(userId, userProfile);
            imageWithTextFile = imageCreatorService.getImageWithTextFile(userProfile.getDescription(), userId);
            replyMessage = favoritesService.getPhotoMessageWithInlineMenuKeyboard(chatId, imageWithTextFile, messageCaption);
        } else if (favoritesList.isEmpty()) {
            replyMessage = favoritesService.getReplyFavoritesKeyboardTextMessage(chatId, MESSAGE_EMPTY);

        } else {
            favoritesDataCache.setProcessDataList(userId, favoritesList);
            UserProfile showFirst = favoritesDataCache.getNextUser(userId);
            messageCaption = createMessageCation(userId, showFirst);
            imageWithTextFile = imageCreatorService.getImageWithTextFile(showFirst.getDescription(), userId);
            replyMessage = favoritesService.getNextPrevInlineKeyboardPhotoMessage(chatId, imageWithTextFile, messageCaption);
            userDataCache.setUsersCurrentBotState(userId, SHOW_NEXT_FAVORITE);
        }

        if (userButtonInput.equals(FAVORITES)) {
            replyMessage = favoritesService.getReplyFavoritesKeyboardTextMessage(chatId, MESSAGE_FAVORITE);
            userDataCache.setUsersCurrentBotState(userId, HANDLER_SHOW_FAVORITES);
        }

        return replyMessage;
    }

    private List<UserProfile> getFavoriteList(String userButtonInput, long userId) {
        switch (userButtonInput) {
            case FAVORITE:
                favoritesDataCache.setFavoriteSearchStatus(userId, CAPTION_FAVORITE);
                return v1RestService.getFavoritesList(userId);
            case ADMIRER:
                favoritesDataCache.setFavoriteSearchStatus(userId, CAPTION_ADMIRER);
                return v1RestService.getAdmirerList(userId);
            case LOVE:
                favoritesDataCache.setFavoriteSearchStatus(userId, CAPTION_LOVE);
                return v1RestService.getLoveList(userId);
            default:
                return new ArrayList<>();
        }
    }
}
