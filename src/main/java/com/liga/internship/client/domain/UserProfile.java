package com.liga.internship.client.domain;

import lombok.*;

import static com.liga.internship.client.commons.Button.*;
import static com.liga.internship.client.commons.TextInput.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class UserProfile {
    private Long telegramId;
    private String username;
    private Integer age = 20;
    private String description;
    private String gender;
    private String speciality;
    private String look;

    public boolean setGender(String gender) {
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

    public boolean setLook(String look) {
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
