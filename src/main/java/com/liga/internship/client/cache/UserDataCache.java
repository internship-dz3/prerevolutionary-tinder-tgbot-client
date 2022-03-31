package com.liga.internship.client.cache;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDataCache {
    private final Map<Long, BotState> userBotStates = new HashMap<>();
    private final Map<Long, UserProfile> userProfileMap = new HashMap<>();

    public UserProfile getUserProfile(long userId) {
        UserProfile userProfileData = userProfileMap.get(userId);
        if (userProfileData == null) {
            userProfileData = new UserProfile();
        }
        return userProfileData;
    }

    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = userBotStates.get(userId);
        if (botState == null) {
            botState = BotState.FILLING_PROFILE_START;
        }
        return botState;
    }

    public void saveUserProfile(long userId, UserProfile userProfile) {
        userProfileMap.put(userId, userProfile);
    }

    public void setUsersCurrentBotState(long userId, BotState botState) {
        userBotStates.put(userId, botState);
    }
}
