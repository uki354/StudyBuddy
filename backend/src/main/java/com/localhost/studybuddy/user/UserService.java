package com.localhost.studybuddy.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    void createUser(UserDto userDto);
    Page<UserModel> findAllByConditions(UserModel userModel, Pageable pageable, int ageGroup);


}
