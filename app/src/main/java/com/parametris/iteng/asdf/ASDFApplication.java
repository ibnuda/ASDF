package com.parametris.iteng.asdf;

import android.app.Application;

import net.gotev.uploadservice.Logger;
import net.gotev.uploadservice.UploadService;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ASDFApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }
}
