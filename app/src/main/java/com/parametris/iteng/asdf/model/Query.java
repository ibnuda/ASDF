package com.parametris.iteng.asdf.model;

public class Query extends Conversation {
    public Query(String name) {
        super(name);
    }

    @Override
    public int getType() {
        return Conversation.TYPE_QUERY;
    }
}
