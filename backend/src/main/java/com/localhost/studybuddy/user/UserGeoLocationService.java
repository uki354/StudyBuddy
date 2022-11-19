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
    public static final String COFFESHOP_REDIS_KEY_INDEX="coffeshop";

    public void addStudyBuddy(GeoLocation geoLocation, int id){
        GeoResult<RedisGeoCommands.GeoLocation<String>> geoLocationGeoResult = validateGeoLocation(geoLocation);
        Point point = geoLocationGeoResult.getContent().getPoint();

        redisTemplate.opsForGeo().add(STUDYBUDDY_REDIS_KEY_INDEX,
                point,
                String.valueOf(id));
    }



    private GeoResult<RedisGeoCommands.GeoLocation<String>> validateGeoLocation(GeoLocation geoLocation){
        GeoResults<RedisGeoCommands.GeoLocation<String>> radius = redisTemplate.opsForGeo().radius(COFFESHOP_REDIS_KEY_INDEX, new Circle(new Point(geoLocation.getLatDouble(), geoLocation.getLngDouble()), new Distance(0.1, Metrics.KILOMETERS)));
        assert radius != null;
        return  radius.getContent().stream().findFirst().orElseThrow(()-> new RuntimeException("Not valid place to be a study buddy"));
    }

    public void removeStudyBuddy(int id){
        redisTemplate.opsForGeo().remove(String.valueOf(id));
    }

    public List<GeoResult<RedisGeoCommands.GeoLocation<String>>> searchForUsers(GeoLocation geoLocation, int distance){
        GeoResults<RedisGeoCommands.GeoLocation<String>> radius =
                redisTemplate.opsForGeo().radius(STUDYBUDDY_REDIS_KEY_INDEX,
                        new Circle(new Point(geoLocation.getLatDouble(),
                                geoLocation.getLngDouble()), new Distance(distance, Metrics.KILOMETERS)), RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates());

        assert radius != null;
        return radius.getContent();
    }













}
