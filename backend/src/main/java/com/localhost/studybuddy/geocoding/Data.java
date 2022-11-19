package com.localhost.studybuddy.geocoding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@JsonIgnoreProperties(ignoreUnknown = true)
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Data {

    private String latitude;
    private String longitude;
    private String label;

}