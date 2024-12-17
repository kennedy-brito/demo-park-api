package com.kennedy.demo_park_api.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpotResponseDto {

    private Long id;
    private String code;
    private String status;
}
