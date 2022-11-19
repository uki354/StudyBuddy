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
public class UserFilteredResponse {

    private Integer id;
    private String name;
    private String email;
    private String lastName;
    private String currentStudyBuddyAddress;
    private String university;
    private String imagePath;
    private Date birthdate;
    private boolean gender;

}
