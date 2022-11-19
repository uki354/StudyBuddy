package com.localhost.studybuddy.user;

import com.localhost.studybuddy.util.GeoLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    void createUser(UserDto userDto);
    Page<UserModel> findAllByConditions(UserModel userModel, Pageable pageable, int ageGroup);
    List<UserModel> findAllUsersWithFiltersApplied(UserModel userModel, Pageable pageable, int ageGroup, GeoLocation geoLocation, int distance);


}
