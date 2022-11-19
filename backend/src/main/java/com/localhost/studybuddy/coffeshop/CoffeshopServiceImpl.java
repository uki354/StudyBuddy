package com.localhost.studybuddy.coffeshop;


import com.localhost.studybuddy.user.UserServiceImpl;
import com.localhost.studybuddy.util.GeoLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CoffeshopServiceImpl {

    private final CoffeshopRepository coffeshopRepository;
    private final UserServiceImpl userService;


    @PostConstruct
    public void init(){
        coffeshopRepository.saveAll(initializeCoffeshops());
        userService.initUsers();
    }

    private List<Coffeshop> initializeCoffeshops(){
        List<Coffeshop> shops = new ArrayList<>();
        shops.add(Coffeshop.builder().geoLocation(new GeoLocation("43.8888068298014","20.337533526592104")).build());
        shops.add(Coffeshop.builder().geoLocation(new GeoLocation("43.89016068660577","20.333778618014403")).build());
        shops.add(Coffeshop.builder().geoLocation(new GeoLocation("43.891188107823815","20.337890777957252")).build());
        shops.add(Coffeshop.builder().geoLocation(new GeoLocation("43.89170172637468","20.339782753919764")).build());
        shops.add(Coffeshop.builder().geoLocation(new GeoLocation("43.891166505515805","20.341910697932924")).build());
        return shops;
    }


}
