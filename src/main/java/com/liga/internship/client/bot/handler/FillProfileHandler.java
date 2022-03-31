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

import static com.liga.internship.client.bot.BotState.*;
import static com.liga.internship.client.commons.TextInput.*;
import static com.liga.internship.client.commons.TextMessage.*;

@Slf4j
@Component
@AllArgsConstructor
public class FillProfileHandler implements InputMessageHandler {
    private final ReplyMessageService messageService;
    private final ProfileService profileService;
    private final UserDataCache userDataCache;
    private final ClientRestService clientRestService;
    private final ImageCreatorService imageCreatorService;
    private final MainMenuService mainMenuService;

    @Override
    public BotState getHandlerName() {
        return FILLING_PROFILE_START;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(FILLING_PROFILE_START)) {
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

        if (botState.equals(FILLING_PROFILE_ASK_GENDER)) {
            userProfile.setTelegramId(userId);
            String[] buttons = {MALE, FEMALE};
            replyToUser = profileService.getChooseButtons(chatId, MESSAGE_CHOOSE_YOUR_GENDER, buttons);
            userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_NAME);
        }
        if (botState.equals(FILLING_PROFILE_ASK_NAME)) {
            if (userProfile.setGender(userAnswer)) {
                replyToUser = messageService.getReplyMessage(chatId, MESSAGE_ENTER_YOUR_NAME);
                userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_DESCRIBE);
            } else {
                String[] buttons = {MALE, FEMALE};
                replyToUser = profileService.getChooseButtons(chatId, MESSAGE_CHOOSE_YOUR_GENDER, buttons);
                userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_NAME);
            }
        }
        if (botState.equals(FILLING_PROFILE_ASK_DESCRIBE)) {
            userProfile.setUsername(userAnswer);
            replyToUser = messageService.getReplyMessage(chatId, MESSAGE_DESCRIBE_YOURSELF);
            userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_ASK_LOOK);
        }
        if (botState.equals(FILLING_PROFILE_ASK_LOOK)) {
            userProfile.setDescription(userAnswer);
            String[] buttons = {MALE, FEMALE, ALL};
            replyToUser = profileService.getChooseButtons(chatId, MESSAGE_LOOKFOR, buttons);
            userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_COMPLETE);
        }
        if (botState.equals(FILLING_PROFILE_COMPLETE)) {
            if (userProfile.setLook(userAnswer)) {
                File imageWithTextFile = imageCreatorService.getImageWithTextFile(userProfile, userId);
                replyToUser = mainMenuService.getMainMenuPhotoMessage(chatId, imageWithTextFile, userProfile.getUsername());
                clientRestService.registerNewUser(userProfile);
                userDataCache.setUsersCurrentBotState(userId, SHOW_MAIN_MENU);
            } else {
                String[] buttons = {MALE, FEMALE, ALL};
                replyToUser = profileService.getChooseButtons(chatId, MESSAGE_LOOKFOR, buttons);
                userDataCache.setUsersCurrentBotState(userId, FILLING_PROFILE_COMPLETE);
            }
        }

        userDataCache.saveUserProfile(userId, userProfile);
        return replyToUser;
    }
}
