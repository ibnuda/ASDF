package com.parametris.iteng.asdf.comm;

import com.parametris.iteng.asdf.ASDF;
import com.parametris.iteng.asdf.model.Server;
import com.parametris.iteng.protocol.IRCClient;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class IRCConnection extends IRCClient {
    public static final String TAG = "Heheh";
    private final IRCService ircService;
    private final Server server;
    private ArrayList<String> autoJoinChannel;
    private Pattern nickMatch;

    private boolean ignoreMTGW = true;
    private boolean debugTraffic = false;
    private boolean quit = false;
    private boolean disposedRequest = false;
    // private final Object quitLock = new Objects();

    public IRCConnection(IRCService ircService, int serverId) {
        this.server = ASDF.getInstance().getServerById(serverId);
        this.ircService = ircService;
        this.debugTraffic = ircService.getSettings().debugTraffic();
    }
}
