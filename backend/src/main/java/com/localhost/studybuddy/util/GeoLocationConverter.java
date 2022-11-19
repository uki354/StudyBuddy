package com.localhost.studybuddy.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class GeoLocationConverter implements AttributeConverter<GeoLocation, String> {

    private static final String SEPARATOR = "/";

    @Override
    public String convertToDatabaseColumn(GeoLocation geoLocation) {
        return  geoLocation.getLatitude() +
                SEPARATOR +
                geoLocation.getLongitude();
    }

    @Override
    public GeoLocation convertToEntityAttribute(String s) {
        String[] locations = s.split(SEPARATOR);
        return new GeoLocation(locations[0],locations[1]);
    }
}
