package com.parametris.iteng.asdf.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

import io.realm.RealmObject;

public class LatLangAtTheTime extends RealmObject {
    public String uuid;
    public Double latitude;
    public Double longitude;

    public void setUuid() {
        this.uuid = UUID.randomUUID().toString();
    }

    public void setLatLng(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public String getUuid() {
        return this.uuid;
    }

    public LatLng getLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }
}
