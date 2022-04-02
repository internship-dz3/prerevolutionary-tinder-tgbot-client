package com.liga.internship.client.cache;

import com.liga.internship.client.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TinderDataCache {
    private final Map<Long, List<UserProfile>> listForProcess = new HashMap<>();
    private final Map<Long, UserProfile> usersInProcess = new HashMap<>();

    public Optional<UserProfile> getNext(long userId) {
        if (listForProcess.containsKey(userId)) {
            List<UserProfile> profileList = listForProcess.get(userId);
            if (!profileList.isEmpty()) {
                return Optional.of(profileList.remove(0));
            }
        }
        return Optional.empty();
    }

    public UserProfile getUserInProcess(long userId) {
        return usersInProcess.remove(userId);
    }

    public void removeProcessList(Long userId) {
        listForProcess.remove(userId);
    }

    public void setProcessDataList(Long userId, List<UserProfile> userProfiles) {
        listForProcess.put(userId, userProfiles);
    }

    public void setToVoting(long userId, UserProfile userInProcess) {
        usersInProcess.put(userId, userInProcess);
    }
}
