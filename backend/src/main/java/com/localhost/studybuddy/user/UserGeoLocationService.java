package com.localhost.studybuddy.user;

import com.localhost.studybuddy.util.GeoLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<GeoResult<RedisGeoCommands.GeoLocation<String>>> searchForUsers(GeoLocation geoLocation, int distance){
        GeoResults<RedisGeoCommands.GeoLocation<String>> radius =
                redisTemplate.opsForGeo().radius(STUDYBUDDY_REDIS_KEY_INDEX,
                        new Circle(new Point(geoLocation.getLatDouble(),
                                geoLocation.getLngDouble()), new Distance(distance, Metrics.KILOMETERS)));

        return radius.getContent();
    }













}
