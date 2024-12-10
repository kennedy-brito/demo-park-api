package com.kennedy.demo_park_api.web.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserPasswordDto {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
