package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.service.ImageCreatorService;
import com.liga.internship.client.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;

import static com.liga.internship.client.commons.TextInput.CHANGE_PROFILE;
import static com.liga.internship.client.commons.TextInput.MAIN_MENU;

@Component
@AllArgsConstructor
public class ShowProfileHandler implements InputMessageHandler {
    private final UserDataCache dataCache;
    private final ProfileService showProfileService;
    private final ImageCreatorService imageCreatorService;

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_PROFILE;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        UserProfile userProfile = dataCache.getUserProfile(userId);
        File imageWithTextFile = imageCreatorService.getImageWithTextFile(userProfile, userId);
        return showProfileService.getMainMenuPhotoMessage(chatId, imageWithTextFile, userProfile.getUsername(), CHANGE_PROFILE, MAIN_MENU);
    }
}
