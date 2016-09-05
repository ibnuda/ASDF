package com.parametris.iteng.asdf.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

public class Server {
    private int id;
    private String title;
    private String host;
    private int port;
    private String password;
    private String charset;
    private boolean useSSL = false;

    private Identity identity;
    private Authentication authentication;

    private final LinkedHashMap<String, Conversation> conversations = new LinkedHashMap<>();
    private ArrayList<String> autoJoinChannels;
    private ArrayList<String> connectCommands;

    private int status = Status.DISCONNECTED;
    private String selected = "";
    private boolean isForeground = false;
    private boolean mayReconnect = false;

    public Server() {
        conversations.put(ServerInfo.DEFAULT_NAME, new ServerInfo());
        this.selected = ServerInfo.DEFAULT_NAME;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<String> getAutoJoinChannels() {
        return autoJoinChannels;
    }

    public void setAutoJoinChannels(ArrayList<String> autoJoinChannels) {
        this.autoJoinChannels = autoJoinChannels;
    }

    public ArrayList<String> getConnectCommands() {
        return connectCommands;
    }

    public void setConnectCommands(ArrayList<String> connectCommands) {
        this.connectCommands = connectCommands;
    }

    public boolean isForeground() {
        return isForeground;
    }

    public void setForeground(boolean foreground) {
        isForeground = foreground;
    }

    public boolean isMayReconnect() {
        return mayReconnect;
    }

    public void setMayReconnect(boolean mayReconnect) {
        this.mayReconnect = mayReconnect;
    }

    public Collection<Conversation> getConversations() {
        return conversations.values();
    }

    public Conversation getConversation(String name) {
        return conversations.get(name.toLowerCase());
    }

    public void addConversation(Conversation conversation) {
        conversations.put(conversation.getName().toLowerCase(), conversation);
    }

    public void removeConversation(String name) {
        conversations.remove(name.toLowerCase());
    }

    public void clearConversations() {
        conversations.clear();
        conversations.put(ServerInfo.DEFAULT_NAME, new ServerInfo());
        this.selected = ServerInfo.DEFAULT_NAME;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public ArrayList<String> getCurrentChannelNames() {
        ArrayList<String> channels = new ArrayList<>();
        Collection<Conversation> mConversations = this.conversations.values();

        for (Conversation conversation :
                mConversations) {
            if (conversation.getType() == Conversation.TYPE_CHANNEL)
                channels.add(conversation.getName());
        }
        return channels;
    }

    public boolean isDisconnected() {
        return status == Status.DISCONNECTED;
    }

    public boolean isConnected() {
        return status == Status.CONNECTED;
    }
}
