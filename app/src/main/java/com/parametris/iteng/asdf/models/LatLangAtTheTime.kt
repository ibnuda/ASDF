package com.parametris.iteng.asdf.models

import com.google.android.gms.maps.model.LatLng

import java.util.UUID

import io.realm.RealmObject

/**
 * Created by DELL on 8/11/2016.
 */
open class LatLangAtTheTime : RealmObject() {
    open var uuid: String? = null
    open var latitude: Double? = null
    open var longitude: Double? = null

    fun setUuid() {
        this.uuid = UUID.randomUUID().toString()
    }

    var latLng: LatLng
        get() = LatLng(this.latitude!!, this.longitude!!)
        set(latLng) {
            this.latitude = latLng.latitude
            this.longitude = latLng.longitude
        }
}
