package com.parametris.iteng.asdf.comm;

import android.os.Binder;

import com.parametris.iteng.asdf.models.Server;

public class IRCBinder extends Binder {
    private final IRCService ircService;
    public IRCBinder(IRCService ircService) {
        super();
        this.ircService = ircService;
    }

    public void connect(final Server server) {
        ircService.connect(server);
    }

    public IRCService getIrcService() {
        return ircService;
    }
}
