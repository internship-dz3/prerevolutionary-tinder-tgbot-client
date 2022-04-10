package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.service.ImageCreatorService;
import com.liga.internship.client.service.ProfileService;
import com.liga.internship.client.service.TextService;
import com.liga.internship.client.service.V1RestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.Optional;

import static com.liga.internship.client.bot.BotState.*;
import static com.liga.internship.client.commons.ButtonCallback.CALLBACK_MALE;
import static com.liga.internship.client.commons.ButtonInput.FEMALE;
import static com.liga.internship.client.commons.ButtonInput.MALE;
import static com.liga.internship.client.commons.TextMessage.*;

/**
 * Обработчик входящих Message сообщений телеграм бота, связанных с заполнением профиля пользователя.
 * Обработчик хранит состояние просматриваемых данных.
 */
@Slf4j
@Component
@AllArgsConstructor
public class FillProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ProfileService profileService;
    private final V1RestService v1RestService;
    private final ImageCreatorService imageCreatorService;
    private final TextService textService;

    @Override
    public BotState getHandlerName() {
        return HANDLER_PROFILE_FILLING;
    }


    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(HANDLER_PROFILE_FILLING)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), FILLING_PROFILE_ASK_GENDER);
        }
        return processUserInput(message);
    }

    private PartialBotApiMethod<?> processUserInput(Message message) {
        String userAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        UserProfile userProfile = userDataCache.getUserProfile(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        PartialBotApiMethod<?> replyToUser = null;
        File imageWithTextFile;

        if (botState.equals(FILLING_PROFILE_ASK_GENDER)) {
            userProfile.setTelegramId(userId);
            replyToUser = profileService.getMessageWithGenderChooseKeyboard(chatId, MESSAGE_CHOOSE_YOUR_GENDER);
            userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_NAME);
        }
        if (botState.equals(FILLING_PROFILE_ASK_NAME)) {
            replyToUser = getMessageReplyForAskName(userAnswer, userId, chatId, userProfile);
        }
        if (botState.equals(FILLING_PROFILE_ASK_DESCRIBE)) {
            userProfile.setUsername(userAnswer);
            replyToUser = profileService.getReplyMessage(chatId, MESSAGE_DESCRIBE_YOURSELF);
            userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_LOOK);
        }
        if (botState.equals(FILLING_PROFILE_ASK_LOOK)) {
            if (userAnswer.length() > 300) {
                replyToUser = profileService.getReplyMessage(chatId, MESSAGE_MANY_WORDS);
                userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_LOOK);
            } else {
                userProfile.setDescription(userAnswer);
                replyToUser = profileService.getMessageWithgetLookGenderChooseKeyboard(chatId, MESSAGE_LOOKFOR);
                userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_COMPLETE);
            }
        }
        if (botState.equals(FILLING_PROFILE_COMPLETE)) {
            if (userProfile.setLookByButtonCallback(userAnswer)) {
                userProfile = getRegisteredUserProfile(userProfile);
                imageWithTextFile = imageCreatorService.getImageWithTextFile(userProfile.getDescription(), userId);
                replyToUser = profileService.getMainMenuPhotoMessage(chatId, imageWithTextFile, getCaptureFromUserProfile(userProfile));
                userDataCache.setUsersCurrentBotState(userId, HANDLER_MAIN_MENU);
            } else {
                replyToUser = profileService.getMessageWithgetLookGenderChooseKeyboard(chatId, MESSAGE_LOOKFOR);
                userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_COMPLETE);
            }
        }
        userDataCache.saveUserProfile(userId, userProfile);
        return replyToUser;
    }

    private PartialBotApiMethod<?> getMessageReplyForAskName(String userAnswer, long userId, long chatId, UserProfile userProfile) {
        log.debug(userProfile.getUsername(), userId, chatId + " getMessageReplyForAskName");
        PartialBotApiMethod<?> replyToUser;
        if (userProfile.setGenderByButtonCallback(userAnswer)) {
            replyToUser = profileService.getReplyMessage(chatId, MESSAGE_ENTER_YOUR_NAME);
            userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_DESCRIBE);
        } else {
            replyToUser = profileService.getMessageWithGenderChooseKeyboard(chatId, MESSAGE_CHOOSE_YOUR_GENDER);
            userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_NAME);
        }
        return replyToUser;
    }

    private UserProfile getRegisteredUserProfile(UserProfile userProfile) {
        log.debug(userProfile.getUsername() + " getRegisteredUserProfile");
        Optional<UserProfile> optionalNewUser = v1RestService.registerNewUser(userProfile);
        return optionalNewUser.orElseThrow();
    }

    private String getCaptureFromUserProfile(UserProfile userProfile) {
        log.debug(userProfile.getUsername() + " getCaptureFromUserProfile");
        String gender = userProfile.getGender().equals(CALLBACK_MALE) ? MALE : FEMALE;
        String username = textService.translateTextIntoSlavOld(userProfile.getUsername());
        return String.format("%s, %s", gender, username);
    }
}
