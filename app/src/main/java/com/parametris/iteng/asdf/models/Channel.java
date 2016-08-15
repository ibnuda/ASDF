package com.parametris.iteng.asdf.models;

public class Channel extends Conversation {

    private String topic;

    @Override
    public int getType() {
        return 0;
    }

    public Channel(String name) {
        super(name);
        this.topic = "";
    }
}
