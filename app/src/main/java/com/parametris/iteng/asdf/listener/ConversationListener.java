package com.parametris.iteng.asdf.listener;

public interface ConversationListener {
    public void onConversationMessage();
    public void onNewConversation();
    public void onRemoveConversation();
    public void onTopicChanged();
}
