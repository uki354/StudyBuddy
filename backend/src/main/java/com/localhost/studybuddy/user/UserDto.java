package com.localhost.studybuddy.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {

    private String email;
    private String password;
    private String name;
    private String lastname;
    private Boolean gender;
    private String university;
    private Date birthdate;

}
