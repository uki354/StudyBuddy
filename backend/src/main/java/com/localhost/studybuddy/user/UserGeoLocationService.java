package com.localhost.studybuddy.user;

import com.localhost.studybuddy.util.GeoLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserGeoLocationService {

    private final RedisTemplate<String, String> redisTemplate;

    public static final String STUDYBUDDY_REDIS_KEY_INDEX = "studdybuddy";


    //validate coffe shop location before adding
    public void addStudyBuddy(GeoLocation geoLocation, int id){
        redisTemplate.opsForGeo().add(STUDYBUDDY_REDIS_KEY_INDEX,
                new Point(geoLocation.getLatDouble(),
                        geoLocation.getLngDouble()),
                String.valueOf(id));
    }

    public void removeStudyBuddy(int id){
        redisTemplate.opsForGeo().remove(String.valueOf(id));
    }












}
