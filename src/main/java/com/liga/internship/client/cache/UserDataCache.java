package com.liga.internship.client.cache;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.domain.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Данные хранящиеся в памяти для хранения активных пользователей и хранения их состояния
 */
@Slf4j
@Component
public class UserDataCache {
    private final Map<Long, BotState> userBotStates = new HashMap<>();
    private final Map<Long, UserProfile> userProfileMap = new HashMap<>();

    /**
     * Получение опционального пользователя
     *
     * @param userId - ID юзера
     * @return Optional.empty() если данный пользватель отсутствует в кэше
     */
    public UserProfile getUserProfile(long userId) {
        return userProfileMap.get(userId);
    }

    /**
     * Получение актуального состояния пользователя
     *
     * @param userId - ID юзера
     * @return актуальное состояние, если пользователь без состояния, состояние устанавливается в состояние логина
     */
    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = userBotStates.get(userId);
        if (botState == null || userProfileMap.get(userId) == null) {
            botState = BotState.HANDLER_LOGIN;
        }
        return botState;
    }

    /**
     * Сохранение активного пользователя в кэш
     *
     * @param userId-     ID юзера
     * @param userProfile - активный профиль
     */
    public void saveUserProfile(long userId, UserProfile userProfile) {
        log.debug("save user profile userId: {}, profile: {}", userId, userProfile);
        userProfileMap.put(userId, userProfile);
    }

    /**
     * Сохранение актуального состояния пользователя
     *
     * @param userId-  ID юзера
     * @param botState - состояние
     */
    public void setUsersCurrentBotState(long userId, BotState botState) {
        log.debug("userId: {}, set BotState: {}", userId, botState.name());
        userBotStates.put(userId, botState);
    }

}
