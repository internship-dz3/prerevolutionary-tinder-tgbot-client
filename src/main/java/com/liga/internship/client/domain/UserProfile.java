package com.liga.internship.client.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class UserProfile {
    private Long id;
    private String username;
    private Integer age = 20;
    private String description;
    private String gender;
    private String speciality;
    private String look;
}
