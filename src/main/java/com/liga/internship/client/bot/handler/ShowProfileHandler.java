package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.service.ImageCreatorService;
import com.liga.internship.client.service.ProfileService;
import com.liga.internship.client.service.TextService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;

import static com.liga.internship.client.commons.ButtonCallback.CALLBACK_FEMALE;
import static com.liga.internship.client.commons.ButtonCallback.CALLBACK_MALE;
import static com.liga.internship.client.commons.ButtonInput.MALE;

/**
 * Обработчик входящих Messageсообщений телеграм бота, связанных с просмотром профиля пользователя и соответствующего меню.
 * Обработчик хранит состояние просматриваемых данных.
 */
@Component
@AllArgsConstructor
public class ShowProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ProfileService showProfileService;
    private final ImageCreatorService imageCreatorService;
    private final TextService textService;

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_PROFILE;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        UserProfile userProfile = userDataCache.getUserProfile(userId);
        File imageWithTextFile = imageCreatorService.getImageWithTextFile(userProfile.getDescription(), userId);
        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_USER_PROFILE);
        return showProfileService.getProfileTextMessageWihProfileMenu(chatId, imageWithTextFile, getCaptureFromUserProfile(userProfile));
    }

    private String getCaptureFromUserProfile(UserProfile userProfile) {
        String gender = userProfile.getGender().equals(CALLBACK_MALE) ? MALE : CALLBACK_FEMALE;
        String username = textService.translateTextIntoSlavOld(userProfile.getUsername());
        return String.format("%s, %s", gender, username);
    }

}
