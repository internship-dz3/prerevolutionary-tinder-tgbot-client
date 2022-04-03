package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.service.ImageCreatorService;
import com.liga.internship.client.service.MainMenuService;
import com.liga.internship.client.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.Optional;

import static com.liga.internship.client.bot.BotState.HANDLER_LOGIN;
import static com.liga.internship.client.commons.TextMessage.MESSAGE_COMEBACK;

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
    private final MainMenuService mainMenuService;

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_PROFILE;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
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
        File imageWithTextFile = imageCreatorService.getImageWithTextFile(userProfile, userId);
        return showProfileService.getProfileTextMessageWihProfileMenu(chatId, imageWithTextFile, userProfile.getUsername());
    }
}
