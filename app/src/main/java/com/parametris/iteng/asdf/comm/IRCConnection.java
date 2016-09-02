package com.parametris.iteng.asdf.comm;

import android.content.Intent;

import com.parametris.iteng.asdf.ASDF;
import com.parametris.iteng.asdf.model.Broadcast;
import com.parametris.iteng.asdf.model.Channel;
import com.parametris.iteng.asdf.model.Conversation;
import com.parametris.iteng.asdf.model.Message;
import com.parametris.iteng.asdf.model.Query;
import com.parametris.iteng.asdf.model.Server;
import com.parametris.iteng.asdf.model.ServerInfo;
import com.parametris.iteng.asdf.model.Status;
import com.parametris.iteng.protocol.IRCClient;

import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;
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
    private final Object quitLock = new Object();

    public IRCConnection(IRCService ircService, int serverId) {
        this.server = ASDF.getInstance().getServerById(serverId);
        this.ircService = ircService;
        this.debugTraffic = ircService.getSettings().debugTraffic();
        this.updateNickMatchPattern();
    }

    public void setNickname(String nickname) {
        this.setName(nickname);
        this.updateNickMatchPattern();
    }

    public void setIdent(String ident) {
        this.setLogin(ident);
    }

    @Override
    protected void onConnect() {
        server.setStatus(Status.CONNECTED);
        server.setMayReconnect(true);
        ignoreMTGW = ircService.getSettings().isIgnoreMOTDEnabled();
        ircService.sendBroadcast(Broadcast.createServerIntent(Broadcast.SERVER_UPDATE, server.getId()));
        ircService.notifyConnected(server.getTitle());

        Message message = new Message("tersambung pada : " + server.getTitle());
        message.setColor(Message.COLOR_RED);
        server.getConversation(ServerInfo.DEFAULT_NAME).addMessage(message);

        Intent intent = Broadcast.createConversationIntent(Broadcast.CONVERSATION_MESSAGE, server.getId(), ServerInfo.DEFAULT_NAME);

        if (server.getAuthentication().hasNickservCredentials()) {
            identify(server.getAuthentication().getNickservPassword());
        }

        ircService.sendBroadcast(intent);
    }

    @Override
    protected void onAction(String sourceNick, String sourceLogin, String sourceHostname, String target, String substring) {
        Conversation conversation;
        Message message = new Message(sourceNick + " " + substring);
        String queryNick = target;

        if (queryNick.equals(this.getNick())) {
            queryNick = sourceNick;
        }

        conversation = server.getConversation(queryNick);

        if (null == conversation) {
            conversation = new Query(queryNick);
            conversation.setHistorySize(ircService.getSettings().getHistorySize());
            server.addConversation(conversation);
            conversation.addMessage(message);

            Intent intent = Broadcast.createConversationIntent(Broadcast.CONVERSATION_NEW, server.getId(), queryNick);
            ircService.sendBroadcast(intent);
        } else {
            conversation.addMessage(message);
            Intent intent = Broadcast.createConversationIntent(Broadcast.CONVERSATION_MESSAGE, server.getId(), queryNick);
            ircService.sendBroadcast(intent);
        }

        if (sourceNick.equals(this.getNick())) {
            return;
        }

        boolean mentioned = isMentioned(substring);
        if (mentioned || target.equals(this.getNick()) || conversation.shouldAlwaysNotify()) {
            if (conversation.getStatus() != Conversation.STATUS_SELECTED || !server.isForeground()) {
                ircService.addNewMention(
                        server.getId(),
                        conversation,
                        conversation.getName() + ": " + sourceNick + " " + substring,
                        ircService.getSettings().isVibrateHighlightEnabled(),
                        ircService.getSettings().isSoundHighlightEnabled(),
                        ircService.getSettings().isLedHighlightEnabled()
                );
            }
        }

        if (mentioned) {
            message.setColor(Message.COLOR_BLUE);
            conversation.setStatus(Conversation.STATUS_HIGHLIGHT);
        }
    }

    @Override
    protected void onInvite(String target, String sourceNick, String sourceLogin, String sourceHostname, String substring) {
        if (target.equals(this.getNick())) {
            Message message = new Message(sourceLogin + " invites you into " + substring);
            server.getConversation(server.getSelected()).addMessage(message);
            Intent intent = Broadcast.createConversationIntent(
                    Broadcast.CONVERSATION_MESSAGE,
                    server.getId(),
                    server.getSelected()
            );
            ircService.sendBroadcast(intent);
        } else {
            Message message = new Message(sourceLogin + " invites " + sourceNick + " into " + substring);
            server.getConversation(substring).addMessage(message);
            Intent intent = Broadcast.createConversationIntent(
                    Broadcast.CONVERSATION_MESSAGE,
                    server.getId(),
                    substring
            );
            ircService.sendBroadcast(intent);
        }
    }

    @Override
    protected void onJoin(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
        if (sourceNick.equalsIgnoreCase(getNick()) && null == server.getConversation(channel)) {
            Conversation conversation = new Channel(channel);
            conversation.setHistorySize(ircService.getSettings().getHistorySize());
            server.addConversation(conversation);
            Intent intent = Broadcast.createConversationIntent(
                    Broadcast.CONVERSATION_NEW,
                    server.getId(),
                    channel
            );
            ircService.sendBroadcast(intent);
        } else if (ircService.getSettings().showJoinPartAndQuit()) {
            Message message = new Message(sourceLogin + " joined.", Message.TYPE_MISC);
            server.getConversation(channel).addMessage(message);
            Intent intent = Broadcast.createConversationIntent(
                    Broadcast.CONVERSATION_MESSAGE,
                    server.getId(),
                    channel
            );
            ircService.sendBroadcast(intent);
        }
    }

    @Override
    protected void onKick(String target, String sourceNick, String sourceLogin, String sourceHostname, String recipient, String substring) {
        if (recipient.equals(getNick())) {
        }
    }

    private boolean isMentioned(String text) {
        return nickMatch.matcher(text).find();
    }

    private void updateNickMatchPattern() {
        nickMatch = Pattern.compile("(?:^|[\\s?!'�:;,.])"+Pattern.quote(getNick())+"(?:[\\s?!'�:;,.]|$)", Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void dispose() {
        synchronized (quitLock) {
            if (quit) {
                disposedRequest = true;
            } else {
                super.dispose();
            }
        }
    }
}
