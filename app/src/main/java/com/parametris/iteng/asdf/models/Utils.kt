package com.parametris.iteng.asdf.models

import com.google.android.gms.maps.model.LatLng


import io.realm.Realm
import io.realm.RealmResults

class Utils {

    fun addLatLangAtTheTime(realm: Realm, latLng: LatLng) {
        realm.executeTransaction { realm ->
            val latLangAtTheTime = realm.createObject(LatLangAtTheTime::class.java)
            latLangAtTheTime.setUuid()
            latLangAtTheTime.latLng = latLng
        }
    }

    fun getAllLatLangAtTheTime(realm: Realm): RealmResults<LatLangAtTheTime> {
        return realm.where(LatLangAtTheTime::class.java).findAll()
    }
}
