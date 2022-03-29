package com.liga.internship.client.domain.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class UserTo {
    private Long id;
    private String username;
    private String description;
    private String gender;
}
