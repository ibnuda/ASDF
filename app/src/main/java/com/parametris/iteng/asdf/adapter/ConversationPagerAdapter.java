package com.parametris.iteng.asdf.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.parametris.iteng.asdf.listener.MessageClickListener;
import com.parametris.iteng.asdf.model.Conversation;
import com.parametris.iteng.asdf.model.Message;
import com.parametris.iteng.asdf.model.Server;
import com.parametris.iteng.asdf.view.MessageListView;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created on 26/08/2016.
 */
public class ConversationPagerAdapter extends PagerAdapter {
    private final Server server;
    private LinkedList<ConversationInfo> conversationInfo;
    private final HashMap<Integer, View> viewHashMap;

    public int getItemPositionByName(String target) {
        int size = conversationInfo.size();
        LinkedList<ConversationInfo> items = this.conversationInfo;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).conversation.getName().equalsIgnoreCase(target)) {
                return i;
            }
        }
        return -1;
    }

    public class ConversationInfo {
        public Conversation conversation;
        public MessageListAdapter messageListAdapter;
        public MessageListView messageListView;

        public ConversationInfo(Conversation conversation) {
            this.conversation = conversation;
            this.messageListAdapter = null;
            this.messageListView = null;
        }
    }

    public ConversationPagerAdapter(Context context, Server server) {
        this.server = server;
        conversationInfo = new LinkedList<>();
        viewHashMap = new HashMap<>();
    }

    public void addConversation(Conversation conversation) {
        conversationInfo.add(new ConversationInfo(conversation));
        notifyDataSetChanged();
    }

    public void removeConversation(int i) {
        conversationInfo.remove(i);
        notifyDataSetChanged();
    }

    public Conversation getItem(int position) {
        ConversationInfo conversationInfo = getItemInfo(position);
        if (null != conversationInfo) {
            return conversationInfo.conversation;
        } else {
            return null;
        }
    }

    public MessageListAdapter getItemAdapter(int position) {
        ConversationInfo conversationInfo = getItemInfo(position);
        if (null != conversationInfo) {
            return conversationInfo.messageListAdapter;
        } else {
            return null;
        }
    }

    public MessageListAdapter getItemAdapter(String name) {
        return getItemAdapter(getPositionByName(name));
    }

    private ConversationInfo getItemInfo(int position) {
        if (0 <= position && position <= conversationInfo.size()) {
            return conversationInfo.get(position);
        }
        return null;
    }

    public int getPositionByName(String name) {
        int size = conversationInfo.size();
        LinkedList<ConversationInfo> items = this.conversationInfo;
        for (int i = 0; i < size; i++) {
            if (items.get(i).conversation.getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void clearConversation() {
        conversationInfo = new LinkedList<>();
    }

    @Override
    public int getCount() {
        return this.conversationInfo.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(View collection, int position) {
        ConversationInfo conversationInfo = this.conversationInfo.get(position);
        View view;

        if (null != conversationInfo.messageListView) {
            view = conversationInfo.messageListView;
        } else {
            view = renderConversation(conversationInfo, collection);
        }

        viewHashMap.put(position, view);
        ((ViewPager) collection).addView(view);

        return view;
    }

    private MessageListView renderConversation(ConversationInfo conversationInfo, View collection) {
        MessageListView messageListView = new MessageListView(collection.getContext());
        conversationInfo.messageListView = messageListView;
        messageListView.setOnItemClickListener(MessageClickListener.getInstance());

        MessageListAdapter messageListAdapter = conversationInfo.messageListAdapter;
        if (null == messageListAdapter) {
            messageListAdapter = new MessageListAdapter(conversationInfo.conversation, collection.getContext());
            conversationInfo.messageListAdapter = messageListAdapter;
        }

        messageListView.setAdapter(messageListAdapter);
        messageListView.setSelection(messageListAdapter.getCount() - 1);

        return messageListView;
    }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
        this.viewHashMap.remove(view);
    }

    @Override
    public String getPageTitle(int position) {
        Conversation conversation = getItem(position);
        if (conversation.getType() == Conversation.TYPE_SERVER) {
            return server.getTitle();
        } else {
            return conversation.getName();
        }
    }
}
