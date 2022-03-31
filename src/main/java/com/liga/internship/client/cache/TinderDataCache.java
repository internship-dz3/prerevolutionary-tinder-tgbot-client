package com.liga.internship.client.cache;

import com.liga.internship.client.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TinderDataCache {
    private final Map<Long, List<UserProfile>> usersForProcess = new HashMap<>();
    private final Map<Long, UserProfile> votingInProgress = new HashMap<>();

    public List<UserProfile> getProcessDataList(Long userId) {
        return usersForProcess.getOrDefault(userId, new LinkedList<>());
    }

    public Optional<UserProfile> getUserProfile(Long userId) {
        if (votingInProgress.containsKey(userId)) {
            return Optional.of(votingInProgress.remove(userId));
        }
        return Optional.empty();
    }

    public void setProcessDataList(Long userId, List<UserProfile> userProfiles) {
        usersForProcess.put(userId, userProfiles);
    }

    public void setToVoting(long userId, UserProfile userInProcess) {
        votingInProgress.put(userId, userInProcess);
    }
}
