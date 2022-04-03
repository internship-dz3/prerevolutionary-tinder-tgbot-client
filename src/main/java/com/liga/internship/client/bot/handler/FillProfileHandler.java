package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.Optional;

import static com.liga.internship.client.bot.BotState.*;
import static com.liga.internship.client.commons.TextMessage.*;

/**
 * Обработчик входящих Message сообщений телеграм бота, связанных с заполнением профиля пользователя.
 * Обработчик хранит состояние просматриваемых данных.
 */
@Slf4j
@Component
@AllArgsConstructor
public class FillProfileHandler implements InputMessageHandler {
    private final ReplyMessageService replyMessageService;
    private final ProfileService profileService;
    private final UserDataCache userDataCache;
    private final V1RestService v1RestService;
    private final ImageCreatorService imageCreatorService;
    private final MainMenuService mainMenuService;

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
        Optional<UserProfile> optionalUserProfile = userDataCache.getUserProfile(userId);
        UserProfile userProfile;
        if (optionalUserProfile.isPresent()) {
            userProfile = optionalUserProfile.get();
        } else {
            userDataCache.setUsersCurrentBotState(userId, HANDLER_LOGIN);
            return mainMenuService.getMainMenuMessage(chatId, MESSAGE_COMEBACK);
        }
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        PartialBotApiMethod<?> replyToUser = null;

        if (botState.equals(FILLING_PROFILE_ASK_GENDER)) {
            userProfile.setTelegramId(userId);
            replyToUser = profileService.getMessageWithGenderChooseKeyboard(chatId, MESSAGE_CHOOSE_YOUR_GENDER);
            userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_NAME);
        }
        if (botState.equals(FILLING_PROFILE_ASK_NAME)) {
            if (userProfile.setGenderByButtonCallback(userAnswer)) {
                replyToUser = replyMessageService.getReplyMessage(chatId, MESSAGE_ENTER_YOUR_NAME);
                userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_DESCRIBE);
            } else {
                replyToUser = profileService.getMessageWithGenderChooseKeyboard(chatId, MESSAGE_CHOOSE_YOUR_GENDER);
                userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_NAME);
            }
        }
        if (botState.equals(FILLING_PROFILE_ASK_DESCRIBE)) {
            userProfile.setUsername(userAnswer);
            replyToUser = replyMessageService.getReplyMessage(chatId, MESSAGE_DESCRIBE_YOURSELF);
            userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_LOOK);
        }
        if (botState.equals(FILLING_PROFILE_ASK_LOOK)) {
            userProfile.setDescription(userAnswer);
            replyToUser = profileService.getMessageWithgetLookGenderChooseKeyboard(chatId, MESSAGE_LOOKFOR);
            userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_COMPLETE);
        }
        if (botState.equals(FILLING_PROFILE_COMPLETE)) {
            if (userProfile.setLookByButtonCallback(userAnswer)) {
                File imageWithTextFile = imageCreatorService.getImageWithTextFile(userProfile, userId);
                replyToUser = mainMenuService.getMainMenuPhotoMessage(chatId, imageWithTextFile, userProfile.getUsername());
                if (userProfile.getId() != null) {
                    v1RestService.updateUser(userProfile);
                } else {
                    Optional<UserProfile> optionalNewUser = v1RestService.registerNewUser(userProfile);
                    if (optionalNewUser.isPresent()) {
                        userDataCache.setUsersCurrentBotState(userId, HANDLER_LOGIN);
                        return mainMenuService.getMainMenuMessage(chatId, MESSAGE_COMEBACK);
                    }
                }
                userDataCache.setUsersCurrentBotState(userId, HANDLER_MAIN_MENU);
            } else {
                replyToUser = profileService.getMessageWithgetLookGenderChooseKeyboard(chatId, MESSAGE_LOOKFOR);
                userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_COMPLETE);
            }
        }

        userDataCache.saveUserProfile(userId, userProfile);
        return replyToUser;
    }
}
