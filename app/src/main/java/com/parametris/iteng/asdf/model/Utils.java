package com.parametris.iteng.asdf.model;

import com.google.android.gms.maps.model.LatLng;


import io.realm.Realm;
import io.realm.RealmResults;

public class Utils {

    public void addLatLangAtTheTime(Realm realm, final LatLng latLng) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                LatLangAtTheTime latLangAtTheTime = realm.createObject(LatLangAtTheTime.class);
                latLangAtTheTime.setUuid();
                latLangAtTheTime.setLatLng(latLng);
            }
        });
    }

    public RealmResults<LatLangAtTheTime> getAllLatLangAtTheTime(Realm realm) {
        return realm.where(LatLangAtTheTime.class).findAll();
    }
}
