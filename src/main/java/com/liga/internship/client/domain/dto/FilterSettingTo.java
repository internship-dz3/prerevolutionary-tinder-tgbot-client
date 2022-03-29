package com.liga.internship.client.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class FilterSettingTo {
    private Integer minAge;
    private Integer maxAge;
    private String gender;
}
