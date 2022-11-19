package com.localhost.studybuddy.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GeoLocation {

    private String latitude;
    private String longitude;

    public GeoLocation(Point point){
        this.latitude = String.valueOf(point.getX());
        this.longitude = String.valueOf(point.getY());

    }

    public double getLatDouble(){
        return Double.parseDouble(latitude);
    }

    public double getLngDouble(){
        return Double.parseDouble(longitude);
    }




}
