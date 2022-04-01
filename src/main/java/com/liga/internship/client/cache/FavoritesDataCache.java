package com.liga.internship.client.cache;

import com.liga.internship.client.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FavoritesDataCache {
    private final Map<Long, List<UserProfile>> listForProcess = new HashMap<>();

    public UserProfile getNextUser(long userId) {
        List<UserProfile> userProfiles = listForProcess.get(userId);
        UserProfile userProfile = userProfiles.remove(0);
        userProfiles.add(userProfile);
        return userProfile;
    }

    public UserProfile getPrevious(long userId) {
        List<UserProfile> userProfiles = listForProcess.get(userId);
        UserProfile userProfile = userProfiles.remove(userProfiles.size() - 1);
        userProfiles.add(0, userProfile);
        return userProfile;
    }

    public void removeProcessList(long userId) {
        listForProcess.remove(userId);
    }

    public void setProcessDataList(Long userId, List<UserProfile> userProfiles) {
        listForProcess.put(userId, userProfiles);
    }
}
