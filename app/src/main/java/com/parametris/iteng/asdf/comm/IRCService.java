package com.parametris.iteng.asdf.comm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class IRCService extends Service {
    public IRCService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}