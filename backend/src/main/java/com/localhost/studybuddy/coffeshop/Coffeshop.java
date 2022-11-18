package com.localhost.studybuddy.coffeshop;

import com.localhost.studybuddy.util.BaseModel;
import com.localhost.studybuddy.util.GeoLocation;
import com.localhost.studybuddy.util.GeoLocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Coffeshop extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Convert(converter = GeoLocationConverter.class)
    private GeoLocation geoLocation;
    private String addressName;



}
