package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.TinderDataCache;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.domain.dto.UsersIdTo;
import com.liga.internship.client.service.*;
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
import static com.liga.internship.client.commons.ButtonInput.*;
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
    private final TextService textService;

    @Override
    public BotState getHandlerName() {
        return HANDLER_TINDER;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        String answer = message.getText();
        if (answer.equals(SEARCH)) {
            return startVoting(userId, chatId);
        }
        return mainMenuService.getMainMenuMessage(chatId, MESSAGE_MAIN_MENU);
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
            userReply = tinderService.getLikeDislikeMenuPhotoMessage(chatId, imageWithTextFile, getCaptureFromUserProfile(next));
        } else {
            tinderDataCache.setProcessDataList(userId, notRatedUsers);
            UserProfile next = tinderDataCache.getNext(userId).get();
            tinderDataCache.setUserToVotingProcess(userId, next);
            userDataCache.setUsersCurrentBotState(userId, CONTINUE_VOTING);
            File imageWithTextFile = imageCreatorService.getImageWithTextFile(next.getDescription(), userId);
            userReply = tinderService.getLikeDislikeMenuPhotoMessage(chatId, imageWithTextFile, getCaptureFromUserProfile(next));
        }
        return userReply;
    }

    private String getCaptureFromUserProfile(UserProfile userProfile) {
        log.debug(userProfile.getUsername());
        String gender = userProfile.getGender().equals(CALLBACK_MALE) ? MALE : FEMALE;
        String username = textService.translateTextIntoSlavOld(userProfile.getUsername());
        return String.format("%s, %s", gender, username);
    }

    @Override
    public PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery) {
        log.debug(callbackQuery.getMessage().toString());
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
            Optional<UserProfile> favoriteUser = tinderDataCache.getUserFromVotingProcess(userId);
            favoriteUser.ifPresent(userProfile -> v1RestService.sendLikeRequest(new UsersIdTo(currentUser.getTelegramId(), userProfile.getTelegramId())));

        }
        if (callbackQueryData.equals(CALLBACK_DISLIKE)) {
            Optional<UserProfile> favoriteUser = tinderDataCache.getUserFromVotingProcess(userId);
            favoriteUser.ifPresent(userProfile -> v1RestService.sendDislikeRequest(new UsersIdTo(currentUser.getTelegramId(), userProfile.getTelegramId())));
        }
        Optional<UserProfile> next = tinderDataCache.getNext(userId);
        if (next.isPresent()) {
            UserProfile userProfile = next.get();
            File imageWithTextFile = imageCreatorService.getImageWithTextFile(userProfile.getDescription(), userId);
            tinderDataCache.setUserToVotingProcess(userId, userProfile);
            userDataCache.setUsersCurrentBotState(userId, CONTINUE_VOTING);
            reply = tinderService.getEditedLikeDislikePhotoMessage(chatId, messageId, imageWithTextFile, getCaptureFromUserProfile(userProfile));
        } else {
            reply = startVoting(userId, chatId);
        }
        return reply;
    }
}
