package com.parametris.iteng.asdf.models;

public class Query extends Conversation {
    public Query(String name) {
        super(name);
    }

    @Override
    public int getType() {
        return Conversation.TYPE_QUERY;
    }
}
