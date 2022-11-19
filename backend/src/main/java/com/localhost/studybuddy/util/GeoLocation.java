package com.localhost.studybuddy.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GeoLocation {

    private String latitude;
    private String longitude;

    public double getLatDouble(){
        return Double.parseDouble(latitude);
    }

    public double getLngDouble(){
        return Double.parseDouble(longitude);
    }




}
