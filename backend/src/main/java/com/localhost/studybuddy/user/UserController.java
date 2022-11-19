package com.localhost.studybuddy.user;


import com.localhost.studybuddy.util.GeoLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final  UserService userService;
    private final UserGeoLocationService userGeoLocationService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserDto user){
        userService.createUser(user);
    }

    @GetMapping("/search")
    public List<UserFilteredResponse> searchUsersByFilters(@RequestParam(defaultValue = "0",required = false) String ageGroup,
                                                @RequestParam(required = false) Boolean gender,
                                                @RequestParam(required = false) String university,
                                                @RequestParam(defaultValue = "2",required = false) int distance,
                                                @RequestParam(required = false) String lat,
                                                @RequestParam(required = false) String lng){


       return userService.
               findAllUsersWithFiltersApplied(UserModel.builder()
                       .gender(gender)
                       .university(university)
                       .build(),
                       PageRequest.of(0,10),
                       Integer.parseInt(ageGroup),
                       new GeoLocation(lat,lng),
                       distance);
    }

    @GetMapping("/search/redis")
    public void searc(){
        userGeoLocationService.searchForUsers(new GeoLocation("43.89243009781097", "20.343840114901017"), 2);
    }

    @PostMapping("/updateImage")
    public ResponseEntity<?> updateUserImage(@RequestParam("image") MultipartFile image){
        userService.updateUserImage(image);
        return new ResponseEntity<>(HttpStatus.OK);
    }






}
