package com.localhost.studybuddy.user;

import com.localhost.studybuddy.coffeshop.Coffeshop;
import com.localhost.studybuddy.coffeshop.CoffeshopRepository;
import com.localhost.studybuddy.util.GeoLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserGeoLocationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final CoffeshopRepository coffeshopRepository;

    public static final String STUDYBUDDY_REDIS_KEY_INDEX = "studdybuddy";
    public static final String COFFESHOP_REDIS_KEY_INDEX="coffeshop";


    @PostConstruct
    public void init(){
        List<Coffeshop> coffeshops = coffeshopRepository.findAll();
        for (Coffeshop x : coffeshops){
            redisTemplate.opsForGeo().
                    add(COFFESHOP_REDIS_KEY_INDEX,
                            new Point(x.getGeoLocation().getLatDouble(),x.getGeoLocation().getLngDouble()),
                            String.valueOf(x.getId()));
        }
    }

    public void addStudyBuddy(GeoLocation geoLocation, Integer id){
        GeoResult<RedisGeoCommands.GeoLocation<String>> geoLocationGeoResult = validateGeoLocation(geoLocation);
        Point point = geoLocationGeoResult.getContent().getPoint();
        System.out.println(point.getX());

        redisTemplate.opsForGeo().add(STUDYBUDDY_REDIS_KEY_INDEX,
                point,
                String.valueOf(id));
    }



    private GeoResult<RedisGeoCommands.GeoLocation<String>> validateGeoLocation(GeoLocation geoLocation){
        GeoResults<RedisGeoCommands.GeoLocation<String>> radius = redisTemplate.opsForGeo().radius(COFFESHOP_REDIS_KEY_INDEX, new Circle(new Point(geoLocation.getLatDouble(), geoLocation.getLngDouble()), new Distance(0.1, Metrics.KILOMETERS)),RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates());
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
