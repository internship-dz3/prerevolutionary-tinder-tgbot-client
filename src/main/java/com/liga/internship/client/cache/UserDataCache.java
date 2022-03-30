package com.liga.internship.client.cache;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> userBotStates = new HashMap<>();
    private final Map<Long, UserProfile> userProfileMap = new HashMap<>();

    @Override
    public UserProfile getUserProfile(long userId) {
        UserProfile userProfileData = userProfileMap.get(userId);
        if (userProfileData == null) {
            userProfileData = new UserProfile();
        }
        return userProfileData;
    }

    @Override
    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = userBotStates.get(userId);
        if (botState == null) {
            botState = BotState.START_TINDER;
        }
        return botState;
    }

    @Override
    public void saveUserProfile(long userId, UserProfile userProfile) {
        userProfileMap.put(userId, userProfile);
    }

    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        userBotStates.put(userId, botState);
    }
}
