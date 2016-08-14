package com.parametris.iteng.asdf

import android.app.Application

import io.realm.Realm
import io.realm.RealmConfiguration

class ASDFApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val realmConfiguration = RealmConfiguration.Builder(this).build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }
}
