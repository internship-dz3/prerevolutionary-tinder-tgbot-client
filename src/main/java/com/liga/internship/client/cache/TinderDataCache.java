package com.liga.internship.client.cache;

import com.liga.internship.client.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TinderDataCache {
    private final Map<Long, List<UserProfile>> listForProcess = new HashMap<>();
    private final Map<Long, UserProfile> usersInProcess = new HashMap<>();

    public List<UserProfile> getProcessDataList(Long userId) {
        return listForProcess.getOrDefault(userId, new LinkedList<>());
    }

    public void removeProcessList(Long userId) {
        listForProcess.remove(userId);
    }

    public Optional<UserProfile> removeUserFromProcess(Long userId) {
        if (usersInProcess.containsKey(userId)) {
            return Optional.of(usersInProcess.remove(userId));
        }
        return Optional.empty();
    }

    public void setProcessDataList(Long userId, List<UserProfile> userProfiles) {
        listForProcess.put(userId, userProfiles);
    }

    public void setToVoting(long userId, UserProfile userInProcess) {
        usersInProcess.put(userId, userInProcess);
    }
}
