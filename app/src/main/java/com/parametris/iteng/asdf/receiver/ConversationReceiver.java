package com.parametris.iteng.asdf.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parametris.iteng.asdf.listener.ConversationListener;
import com.parametris.iteng.asdf.models.Broadcast;
import com.parametris.iteng.asdf.models.Extra;

public class ConversationReceiver extends BroadcastReceiver {
    private final ConversationListener conversationListener;
    private final int serverId;

    public ConversationReceiver(ConversationListener conversationListener, int serverId) {
        this.conversationListener = conversationListener;
        this.serverId = serverId;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int serverId = intent.getExtras().getInt(Extra.SERVER);
        if (serverId != this.serverId) {
            return;
        }

        String action = intent.getAction();
        String target = intent.getExtras().getString(Extra.CONVERSATION);

        switch (action) {
            case Broadcast.CONVERSATION_MESSAGE:
                conversationListener.onConversationMessage(target);
                break;
            case Broadcast.CONVERSATION_NEW:
                conversationListener.onNewConversation(target);
                break;
            case Broadcast.CONVERSATION_REMOVE:
                conversationListener.onRemoveConversation(target);
                break;
            case Broadcast.CONVERSATION_TOPIC:
                conversationListener.onTopicChanged(target);
                break;
        }
    }
}
