package com.parametris.iteng.asdf.model;

import android.content.Intent;

public abstract class Broadcast {
    public static final String SERVER_UPDATE = "comm.status";
    public static final String SERVER_RECONNECT = "comm.reconnect";
    public static final String CONVERSATION_MESSAGE = "conv.message";
    public static final String CONVERSATION_NEW = "conv.new";
    public static final String CONVERSATION_REMOVE = "conv.remove";
    public static final String CONVERSATION_TOPIC = "conv.topic";

    public static Intent createConversationIntent(String broadcastType, int serverId, String convName) {
        Intent intent = new Intent(broadcastType);
        intent.putExtra(Extra.SERVER, serverId);
        intent.putExtra(Extra.CONVERSATION, convName);

        return intent;
    }

    public static Intent createServerIntent(String broadcastType, int serverId) {
        Intent intent = new Intent(broadcastType);
        intent.putExtra(Extra.SERVER, serverId);
        return intent;
    }
}
