package com.parametris.iteng.protocol;

import java.io.IOException;

public abstract class IRCClient {
    public static final int RPL_LIST = 322;
    public static final int RPL_TOPIC = 332;
    public static final int RPL_TOPICINFO = 333;
    public static final int RPL_NAMREPLY = 353;
    public static final int RPL_ENDOFNAMES = 366;

    public static final int OP_ADD = 1;
    public static final int OP_REMOVE = 2;
    public static final int VOICE_ADD = 3;
    public static final int VOICE_REMOVE = 4;

    private InputThread inputThread = null;
    

    public IRCClient() {}

    public final synchronized void connect(String hostname) throws IOException, IRCException, NickAlreadyInUseException {
        this.connect(hostname, 6667, null);
    }

    private final synchronized void connect(String hostname, int i) throws IOException, IRCException, NickAlreadyInUseException {
        this.connect(hostname, i, null);
    }

    private void connect(String hostname, int i, String password) throws IOException, IRCException, NickAlreadyInUseException {

    }
}
