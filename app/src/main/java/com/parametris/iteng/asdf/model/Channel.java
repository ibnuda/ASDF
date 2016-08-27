package com.parametris.iteng.asdf.model;

public class Channel extends Conversation {

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    private String topic;

    @Override
    public int getType() {
        return Conversation.TYPE_CHANNEL;
    }

    public Channel(String name) {
        super(name);
        this.topic = "";
    }

}
