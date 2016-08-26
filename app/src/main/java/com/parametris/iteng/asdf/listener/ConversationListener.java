package com.parametris.iteng.asdf.listener;

public interface ConversationListener {
    public void onConversationMessage(String target);
    public void onNewConversation(String target);
    public void onRemoveConversation(String target);
    public void onTopicChanged(String target);
}
