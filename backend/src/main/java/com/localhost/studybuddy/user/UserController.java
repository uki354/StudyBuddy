package com.localhost.studybuddy.user;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final  UserService userService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserDto user){
        userService.createUser(user);
    }

    @GetMapping("/search")
    public Page<UserModel> searchUsersByFilters(@RequestParam(defaultValue = "0",required = false) String ageGroup,
                                                @RequestParam(required = false) boolean gender,
                                                @RequestParam(required = false) String university,
                                                @RequestParam(defaultValue = "2000",required = false) String distance){

        UserModel user = UserModel.builder()
                .gender(gender)
                .university(university)
                .build();
        System.out.println(user);
       return userService.
               findAllByConditions(UserModel.builder()
                       .gender(gender)
                       .university(university)
                       .build(),
                       PageRequest.of(0,10),
                       Integer.parseInt(ageGroup));
    }






}
