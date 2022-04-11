package com.liga.internship.client.cache;

import com.liga.internship.client.domain.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Данные хранящиеся в памяти для отображения участников голосования, для соответствующего пользователя
 */
@Slf4j
@Component
public class TinderDataCache {
    private final Map<Long, List<UserProfile>> listForProcess = new HashMap<>();
    private final Map<Long, UserProfile> usersInProcess = new HashMap<>();

    /**
     * Получение следующего опционального пользователя для голосования
     *
     * @param userId - ID активного пользователя(голосующего)
     * @return опциональный пользователь или Optional.empty() при отсутвии
     */
    public Optional<UserProfile> getNext(long userId) {
        if (listForProcess.containsKey(userId)) {
            List<UserProfile> profileList = listForProcess.get(userId);
            if (!profileList.isEmpty()) {
                return Optional.of(profileList.remove(0));
            }
        }
        return Optional.empty();
    }

    /**
     * Получение кандидата находящегося в состоянии голосования
     *
     * @param userId - ID активного пользователя(голосующего)
     * @return - кандидат голосования
     */
    public Optional<UserProfile> getUserFromVotingProcess(long userId) {
        UserProfile remove = usersInProcess.remove(userId);
        if (remove == null) {
            return Optional.empty();
        } else {
            return Optional.of(remove);
        }
    }

    /**
     * Удаление списка голосования из кэша
     *
     * @param userId - ID активного пользователя(голосующего)
     */
    public void removeProcessList(Long userId) {
        log.debug("userId: {}, remove tinder process list", userId);
        listForProcess.remove(userId);
    }

    /**
     * Внесения списка голосования в кэш
     *
     * @param userId       - ID активного пользователя(голосующего)
     * @param userProfiles - список кандидатов голосования
     */
    public void setProcessDataList(Long userId, List<UserProfile> userProfiles) {
        log.debug("userId: {}, set tinder process data list size: {}", userId, userProfiles.size());
        listForProcess.put(userId, userProfiles);
    }

    /**
     * Внесение в кэш кандидата голосования
     *
     * @param userId        - ID активного пользователя(голосующего)
     * @param userInProcess - кандидат голосования
     */
    public void setUserToVotingProcess(long userId, UserProfile userInProcess) {
        log.debug("tinder user process userId: {}, set process user: {}", userId, userInProcess);
        usersInProcess.put(userId, userInProcess);
    }
}
