package com.parametris.iteng.asdf.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parametris.iteng.asdf.comm.IRCService;
import com.parametris.iteng.asdf.model.Broadcast;
import com.parametris.iteng.asdf.model.Server;

public class ReconnectReceiver extends BroadcastReceiver {
    private IRCService service;
    private Server server;

    public ReconnectReceiver(IRCService service, Server server) {
        this.service = service;
        this.server = server;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(Broadcast.SERVER_RECONNECT + server.getId())) {
            return;
        }
        service.connect(server);
    }
}
