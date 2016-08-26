package com.parametris.iteng.asdf.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parametris.iteng.asdf.listener.ServerListener;

public class ServerReceiver extends BroadcastReceiver {
    private final ServerListener serverListener;

    public ServerReceiver(ServerListener serverListener) {
        this.serverListener = serverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        serverListener.onStatusUpdate();
    }
}
