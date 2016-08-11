package com.parametris.iteng.asdf.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by DELL on 8/11/2016.
 */
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

    public RealmResults<LatLangAtTheTime> getAllLatLangsAtTheTime(Realm realm) {
        return realm.where(LatLangAtTheTime.class).findAll();
    }
}
