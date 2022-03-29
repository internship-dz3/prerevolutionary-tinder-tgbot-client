package com.liga.internship.client.handler;

import com.liga.internship.client.domain.dto.UserTo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RegistrationHandler {
    private final String chatId;
    private final UserTo userTo;
    private int messageId;

    public void messageIdIncrease() {
        this.messageId++;
    }

    public void setUserDescription(String description) {
        userTo.setUsername(description);
    }

    public void setUserGender(String gender) {
        userTo.setGender(gender);
    }

    public void setUsername(String name) {
        userTo.setUsername(name);
    }
}
