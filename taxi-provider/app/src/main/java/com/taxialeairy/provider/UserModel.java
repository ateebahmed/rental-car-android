package com.taxialeairy.provider;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserModel {
    public String id;
    public String lat;
    public String lng;

    public UserModel(String id,String lat,String lng){
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

}
