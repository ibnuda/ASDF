package com.parametris.iteng.asdf.models;

/**
 * Created by DELL on 8/15/2016.
 */
public class ServerInfo extends Conversation {

    public static final String DEFAULT_NAME = "";

    public ServerInfo() {
        super(DEFAULT_NAME);
    }

    @Override
    public int getType() {
        return Conversation.TYPE_SERVER;
    }

}
