package com.localhost.studybuddy.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserDto {

    private String email;
    private String password;
    private String name;
    private String lastname;

}
