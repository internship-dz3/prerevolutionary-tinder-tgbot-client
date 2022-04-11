package com.liga.internship.client.cache;

import com.liga.internship.client.domain.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Данные хранящиеся в памяти для отображения любимцев, соответствующего пользователя
 */
@Slf4j
@Component
public class FavoritesDataCache {
    private final Map<Long, List<UserProfile>> userFavorites = new HashMap<>();
    private final Map<Long, String> favoritesListStatus = new HashMap<>();

    public String getFaforiteListStatus(long userId) {
        return favoritesListStatus.get(userId);
    }

    /**
     * Получение следующего пользователя по списку
     *
     * @param userId -ID юзера
     * @return следующий пользователь в списке
     */
    public UserProfile getNextUser(long userId) {
        List<UserProfile> userProfiles = userFavorites.get(userId);
        UserProfile userProfile = userProfiles.remove(0);
        userProfiles.add(userProfile);
        return userProfile;
    }

    /**
     * Получение предыдущего пользователя по списку
     *
     * @param userId -ID юзера
     * @return предыдущий пользователь в списке
     */
    public UserProfile getPrevious(long userId) {
        List<UserProfile> userProfiles = userFavorites.get(userId);
        UserProfile userProfile = userProfiles.remove(userProfiles.size() - 1);
        userProfiles.add(0, userProfile);
        return userProfile;
    }

    /**
     * Удаление списка любимцев из кэша
     *
     * @param userId - ID юзера
     */
    public void removeFaforites(long userId) {
        userFavorites.remove(userId);
    }

    /**
     * @param userId       -ID юзера
     * @param favoriteType - тип поиска любимцев
     */
    public void setFavoriteSearchStatus(long userId, String favoriteType) {
        favoritesListStatus.put(userId, favoriteType);
    }

    /**
     * Добавление списка любимцев в кэш
     *
     * @param userId       - ID юзера
     * @param userProfiles - список любимцев
     */
    public void setProcessDataList(Long userId, List<UserProfile> userProfiles) {
        log.info("FavoritesDataCache setProcessDataList: userId: {}, favorites list size: {}", userId, userProfiles.size());
        userFavorites.put(userId, userProfiles);
    }
}
