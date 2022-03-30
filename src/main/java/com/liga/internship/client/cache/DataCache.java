package com.liga.internship.client.cache;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.domain.UserProfile;

public interface DataCache {
    UserProfile getUserProfile(long userId);

    BotState getUsersCurrentBotState(long userId);

    void saveUserProfile(long userId, UserProfile userProfileData);

    void setUsersCurrentBotState(long userId, BotState botState);
}
