package com.liga.internship.client.cache;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Данные хранящиеся в памяти для хранения активных пользователей и хранения их состояния
 */
@Component
public class UserDataCache {
    private final Map<Long, BotState> userBotStates = new HashMap<>();
    private final Map<Long, UserProfile> userProfileMap = new HashMap<>();
    private final Map<Long, Boolean> isLogged = new HashMap<>();

    /**
     * Получение опционального пользователя
     *
     * @param userId - ID юзера
     * @return Optional.empty() если данный пользватель отсутствует в кэше
     */
    public UserProfile getUserProfile(long userId) {
        UserProfile activeUserProfile = userProfileMap.get(userId);
        if (activeUserProfile == null) {
            return new UserProfile();
        }
        return activeUserProfile;
    }

    /**
     * Получение актуального состояния пользователя
     *
     * @param userId - ID юзера
     * @return актуальное состояние, если пользователь без состояния, состояние устанавливается в состояние логина
     */
    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = userBotStates.get(userId);
        if (botState == null) {
            botState = BotState.HANDLER_LOGIN;
        }
        return botState;
    }

    public boolean isLoggedIn(long userId) {
        return isLogged.get(userId) != null && isLogged.get(userId);
    }

    /**
     * Сохранение активного пользователя в кэш
     *
     * @param userId-     ID юзера
     * @param userProfile - активный профиль
     */
    public void saveUserProfile(long userId, UserProfile userProfile) {
        userProfileMap.put(userId, userProfile);
    }

    public void setLoginUser(long userId, boolean isLogedIn) {
        isLogged.put(userId, isLogedIn);
    }

    /**
     * Сохранение актуального состояния пользователя
     *
     * @param userId-  ID юзера
     * @param botState - состояние
     */
    public void setUsersCurrentBotState(long userId, BotState botState) {
        userBotStates.put(userId, botState);
    }

}
