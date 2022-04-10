package com.liga.internship.client.domain;

import lombok.*;

import static com.liga.internship.client.commons.ButtonCallback.*;
import static com.liga.internship.client.commons.ButtonInput.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class UserProfile {
    private Long telegramId;
    private String username;
    private String description;
    private String gender;
    private String look;

    public boolean setGenderByButtonCallback(String gender) {
        switch (gender) {
            case FEMALE:
                this.gender = CALLBACK_FEMALE;
                return true;
            case MALE:
                this.gender = CALLBACK_MALE;
                return true;
            default:
                return false;
        }
    }

    public boolean setLookByButtonCallback(String look) {
        switch (look) {
            case FEMALE:
                this.look = CALLBACK_FEMALE;
                return true;
            case MALE:
                this.look = CALLBACK_MALE;
                return true;
            case ALL:
                this.look = CALLBACK_ALL;
                return true;
            default:
                return false;
        }
    }
}
