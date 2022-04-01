package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.TinderDataCache;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.domain.dto.UsersIdTo;
import com.liga.internship.client.service.ImageCreatorService;
import com.liga.internship.client.service.MainMenuService;
import com.liga.internship.client.service.TinderService;
import com.liga.internship.client.service.V1RestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static com.liga.internship.client.bot.BotState.*;
import static com.liga.internship.client.commons.Button.*;
import static com.liga.internship.client.commons.TextMessage.MESSAGE_MAIN_MENU;

@Slf4j
@Component
@AllArgsConstructor
public class TinderHandler implements InputMessageHandler, InputCallbackHandler {
    private final UserDataCache dataCache;
    private final TinderService tinderService;
    private final ImageCreatorService imageCreatorService;
    private final MainMenuService mainMenuService;
    private final TinderDataCache tinderDataCache;
    private final V1RestService v1RestService;

    @Override
    public BotState getHandlerName() {
        return START_TINDER;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        List<UserProfile> notRatedUsers = v1RestService.getNotRatedUsers(userId);
        UserProfile userProfile = notRatedUsers.remove(0);
        File imageWithTextFile = imageCreatorService.getImageWithTextFile(userProfile, userId);
        tinderDataCache.setProcessDataList(userId, notRatedUsers);
        tinderDataCache.setToVoting(userId, userProfile);
        dataCache.setUsersCurrentBotState(userId, CONTINUE_VOTING);
        return tinderService.getLikeDislikeMenuPhotoMessage(chatId, imageWithTextFile, userProfile.getUsername());
    }

    @Override
    public PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery) {
        String callbackQueryData = callbackQuery.getData();
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        UserProfile currentUser = dataCache.getUserProfile(userId);

        PartialBotApiMethod<?> reply = null;
        if (callbackQueryData.equals(CALLBACK_MENU)) {
            dataCache.setUsersCurrentBotState(userId, SHOW_MAIN_MENU);
            reply = mainMenuService.getMainMenuMessage(chatId, MESSAGE_MAIN_MENU);
        }

        if (callbackQueryData.equals(CALLBACK_LIKE)) {
            Optional<UserProfile> cacheUserProfile = tinderDataCache.removeUserFromProcess(userId);
            if (cacheUserProfile.isPresent()) {
                UserProfile favoriteUser = cacheUserProfile.get();
                v1RestService.sendLikeRequest(new UsersIdTo(currentUser.getId(), favoriteUser.getId()));
            } else {
                return mainMenuService.getMainMenuMessage(chatId, MESSAGE_MAIN_MENU);
            }

            UserProfile nextToVote = tinderDataCache.getProcessDataList(userId).remove(0);
            File imageWithTextFile = imageCreatorService.getImageWithTextFile(nextToVote, userId);
            tinderDataCache.setToVoting(userId, nextToVote);
            dataCache.setUsersCurrentBotState(userId, CONTINUE_VOTING);
            reply = tinderService.getEditedLikeDislikePhotoMessage(chatId, messageId, imageWithTextFile, nextToVote.getUsername());

        }

        if (callbackQueryData.equals(CALLBACK_DISLIKE)) {
            Optional<UserProfile> cacheUserProfile = tinderDataCache.removeUserFromProcess(userId);
            if (cacheUserProfile.isPresent()) {
                UserProfile favoriteUser = cacheUserProfile.get();
                v1RestService.sendDislikeRequest(new UsersIdTo(currentUser.getId(), favoriteUser.getId()));
            } else {
                return mainMenuService.getMainMenuMessage(chatId, MESSAGE_MAIN_MENU);
            }
            UserProfile nextToVote = tinderDataCache.getProcessDataList(userId).remove(0);
            File imageWithTextFile = imageCreatorService.getImageWithTextFile(nextToVote, userId);
            tinderDataCache.setToVoting(userId, nextToVote);
            dataCache.setUsersCurrentBotState(userId, CONTINUE_VOTING);
            reply = tinderService.getEditedLikeDislikePhotoMessage(chatId, messageId, imageWithTextFile, nextToVote.getUsername());
        }

        return reply;
    }
}
