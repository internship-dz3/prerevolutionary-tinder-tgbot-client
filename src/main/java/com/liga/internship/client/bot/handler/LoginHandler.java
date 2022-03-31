package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.commons.Button;
import com.liga.internship.client.commons.TextMessage;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.service.ClientRestService;
import com.liga.internship.client.service.MainMenuService;
import com.liga.internship.client.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

import static com.liga.internship.client.bot.BotState.LOGIN;

@Component
@AllArgsConstructor
public class LoginHandler implements InputMessageHandler {
    private final ClientRestService clientRestService;
    private final UserDataCache userDataCache;
    private final ProfileService profileService;
    private final MainMenuService mainMenuService;

    @Override
    public BotState getHandlerName() {
        return LOGIN;
    }

    @Override
    public PartialBotApiMethod<?> handleMessage(Message message) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        Optional<UserProfile> userProfile = clientRestService.userLogin(userId);
        if (userProfile.isPresent()) {
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
            userDataCache.saveUserProfile(userId, userProfile.get());
            return mainMenuService.getMainMenuMessage(chatId, TextMessage.MESSAGE_MAIN_MENU);
        }
        userDataCache.setUsersCurrentBotState(userId, BotState.FILLING_PROFILE_START);
        return profileService.getChooseButtons(chatId, TextMessage.MESSAGE_WELCOME, Button.FILL_FORM);
    }
}
