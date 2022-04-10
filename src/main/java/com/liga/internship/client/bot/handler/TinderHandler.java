package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.bot.TinderTelegramBot;
import com.liga.internship.client.cache.TinderDataCache;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.domain.dto.UsersIdTo;
import com.liga.internship.client.service.ImageCreatorService;
import com.liga.internship.client.service.TextService;
import com.liga.internship.client.service.TinderService;
import com.liga.internship.client.service.V1RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
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
import static com.liga.internship.client.commons.Constant.*;

/**
 * Обработчик входящих Message и CallbackQuery сообщений телеграм бота, связанных с голосованием.
 * Обработчик хранит состояние просматриваемых данных.
 */
@Slf4j
@Component
public class TinderHandler implements InputMessageHandler, InputCallbackHandler {
    private final UserDataCache userDataCache;
    private final TinderService tinderService;
    private final ImageCreatorService imageCreatorService;
    private final TinderDataCache tinderDataCache;
    private final V1RestService v1RestService;
    private final TextService textService;
    private final TinderTelegramBot telegramBot;

    public TinderHandler(UserDataCache userDataCache,
                         TinderService tinderService,
                         ImageCreatorService imageCreatorService,
                         TinderDataCache tinderDataCache,
                         V1RestService v1RestService,
                         TextService textService,
                         @Lazy TinderTelegramBot telegramBot) {
        this.userDataCache = userDataCache;
        this.tinderService = tinderService;
        this.imageCreatorService = imageCreatorService;
        this.tinderDataCache = tinderDataCache;
        this.v1RestService = v1RestService;
        this.textService = textService;
        this.telegramBot = telegramBot;
    }

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
        return tinderService.getMainMenuMessage(chatId, MESSAGE_MAIN_MENU);
    }

    @Override
    public PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery) {
        String callbackQueryData = callbackQuery.getData();
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        UserProfile currentUser = userDataCache.getUserProfile(userId);
        if (callbackQueryData.equals(CALLBACK_MENU)) {
            userDataCache.setUsersCurrentBotState(userId, HANDLER_MAIN_MENU);
            return tinderService.getMainMenuMessage(chatId, MESSAGE_MAIN_MENU);
        }
        processVotingCallbackQuery(callbackQuery, callbackQueryData, userId, currentUser);
        return getReplyMessage(userId, chatId, messageId);
    }

    private String getCaptureFromUserProfile(UserProfile userProfile) {
        String gender = userProfile.getGender().equals(CALLBACK_MALE) ? MALE : FEMALE;
        String username = textService.translateTextIntoSlavOld(userProfile.getUsername());
        return String.format("%s, %s", gender, username);
    }

    private PartialBotApiMethod<?> getReplyMessage(long userId, long chatId, int messageId) {
        PartialBotApiMethod<?> reply;
        Optional<UserProfile> nextOptionalUSer = tinderDataCache.getNext(userId);
        if (nextOptionalUSer.isPresent()) {
            UserProfile nextUserForVoting = nextOptionalUSer.get();
            File imageWithTextFile = imageCreatorService.getImageWithTextFile(nextUserForVoting.getDescription(), userId);
            tinderDataCache.setUserToVotingProcess(userId, nextUserForVoting);
            userDataCache.setUsersCurrentBotState(userId, CONTINUE_VOTING);
            reply = tinderService.getEditedLikeDislikePhotoMessage(chatId, messageId, imageWithTextFile, getCaptureFromUserProfile(nextUserForVoting));
        } else {
            reply = startVoting(userId, chatId);
        }
        return reply;
    }

    private void processVotingCallbackQuery(CallbackQuery callbackQuery, String callbackQueryData, long userId, UserProfile currentUser) {
        if (callbackQueryData.equals(CALLBACK_LIKE)) {
            Optional<UserProfile> favoriteUser = tinderDataCache.getUserFromVotingProcess(userId);
            if (favoriteUser.isPresent()) {
                UserProfile userProfile = favoriteUser.get();
                boolean isLove = v1RestService.sendLikeRequest(new UsersIdTo(currentUser.getTelegramId(), userProfile.getTelegramId()));
                if (isLove) {
                    telegramBot.sendNotificationAlert(callbackQuery.getId(), MESSAGE_IS_LOVE);
                }
            }
        }

        if (callbackQueryData.equals(CALLBACK_DISLIKE)) {
            Optional<UserProfile> favoriteUser = tinderDataCache.getUserFromVotingProcess(userId);
            favoriteUser.ifPresent(userProfile -> v1RestService.sendDislikeRequest(new UsersIdTo(currentUser.getTelegramId(), userProfile.getTelegramId())));
        }
    }

    private PartialBotApiMethod<?> startVoting(long userId, long chatId) {
        List<UserProfile> notRatedUsers = v1RestService.getNotRatedUsers(userId);
        PartialBotApiMethod<?> userReply;
        if (notRatedUsers.isEmpty()) {
            tinderDataCache.removeProcessList(userId);
            return tinderService.getMainMenuMessage(chatId, MESSAGE_COMEBACK_LATER);
        }
        tinderDataCache.setProcessDataList(userId, notRatedUsers);
        Optional<UserProfile> optionalNextUser = tinderDataCache.getNext(userId);
        if (optionalNextUser.isPresent()) {
            UserProfile next = optionalNextUser.get();
            tinderDataCache.setUserToVotingProcess(userId, next);
            userDataCache.setUsersCurrentBotState(userId, CONTINUE_VOTING);
            File imageWithTextFile = imageCreatorService.getImageWithTextFile(next.getDescription(), userId);
            userReply = tinderService.getLikeDislikeMenuPhotoMessage(chatId, imageWithTextFile, getCaptureFromUserProfile(next));
        } else {
            userReply = tinderService.getMainMenuMessage(chatId, MESSAGE_COMEBACK_LATER);
        }

        return userReply;
    }
}
