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
import static com.liga.internship.client.commons.ButtonCallback.*;
import static com.liga.internship.client.commons.TextMessage.MESSAGE_COMEBACK_LATER;
import static com.liga.internship.client.commons.TextMessage.MESSAGE_MAIN_MENU;

/**
 * Обработчик входящих Message и CallbackQuery сообщений телеграм бота, связанных с голосованием.
 * Обработчик хранит состояние просматриваемых данных.
 */
@Slf4j
@Component
@AllArgsConstructor
public class TinderHandler implements InputMessageHandler, InputCallbackHandler {
    private final UserDataCache userDataCache;
    private final TinderService tinderService;
    private final ImageCreatorService imageCreatorService;
    private final MainMenuService mainMenuService;
    private final TinderDataCache tinderDataCache;
    private final V1RestService v1RestService;

    @Override
    public BotState getHandlerName() {
        return HANDLER_TINDER;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        return startVoting(userId, chatId);
    }

    private PartialBotApiMethod<?> startVoting(long userId, long chatId) {
        UserProfile userProfile = userDataCache.getUserProfile(userId);
        List<UserProfile> notRatedUsers = v1RestService.getNotRatedUsers(userProfile);
        PartialBotApiMethod<?> userReply;
        if (notRatedUsers.isEmpty()) {
            tinderDataCache.removeProcessList(userId);
            userReply = mainMenuService.getMainMenuMessage(chatId, MESSAGE_COMEBACK_LATER);
        } else if (notRatedUsers.size() == 1) {
            UserProfile next = notRatedUsers.remove(0);
            tinderDataCache.setUserToVotingProcess(userId, next);
            userDataCache.setUsersCurrentBotState(userId, CONTINUE_VOTING);
            File imageWithTextFile = imageCreatorService.getImageWithTextFile(next.getDescription(), userId);
            userReply = tinderService.getLikeDislikeMenuPhotoMessage(chatId, imageWithTextFile, next.getUsername());
        } else {
            tinderDataCache.setProcessDataList(userId, notRatedUsers);
            UserProfile next = tinderDataCache.getNext(userId).get();
            tinderDataCache.setUserToVotingProcess(userId, next);
            userDataCache.setUsersCurrentBotState(userId, CONTINUE_VOTING);
            File imageWithTextFile = imageCreatorService.getImageWithTextFile(next.getDescription(), userId);
            userReply = tinderService.getLikeDislikeMenuPhotoMessage(chatId, imageWithTextFile, next.getUsername());
        }
        return userReply;
    }

    @Override
    public PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery) {
        String callbackQueryData = callbackQuery.getData();
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        UserProfile currentUser = userDataCache.getUserProfile(userId);
        PartialBotApiMethod<?> reply;
        if (callbackQueryData.equals(CALLBACK_MENU)) {
            userDataCache.setUsersCurrentBotState(userId, HANDLER_MAIN_MENU);
            return mainMenuService.getMainMenuMessage(chatId, MESSAGE_MAIN_MENU);
        }
        if (callbackQueryData.equals(CALLBACK_LIKE)) {
            UserProfile favoriteUser = tinderDataCache.getUserFromVotingProcess(userId);
            v1RestService.sendLikeRequest(new UsersIdTo(currentUser.getId(), favoriteUser.getId()));
        }
        if (callbackQueryData.equals(CALLBACK_DISLIKE)) {
            UserProfile favoriteUser = tinderDataCache.getUserFromVotingProcess(userId);
            v1RestService.sendDislikeRequest(new UsersIdTo(currentUser.getId(), favoriteUser.getId()));
        }
        Optional<UserProfile> next = tinderDataCache.getNext(userId);
        if (next.isPresent()) {
            UserProfile userProfile = next.get();
            File imageWithTextFile = imageCreatorService.getImageWithTextFile(userProfile.getDescription(), userId);
            tinderDataCache.setUserToVotingProcess(userId, userProfile);
            userDataCache.setUsersCurrentBotState(userId, CONTINUE_VOTING);
            reply = tinderService.getEditedLikeDislikePhotoMessage(chatId, messageId, imageWithTextFile, userProfile.getUsername());
        } else {
            reply = startVoting(userId, chatId);
        }
        return reply;
    }
}
