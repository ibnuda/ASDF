package com.parametris.iteng.asdf.adapter;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.parametris.iteng.asdf.models.Conversation;
import com.parametris.iteng.asdf.models.Message;

import java.util.LinkedList;

public class MessageListAdapter extends BaseAdapter {

    private final LinkedList<Message> messages;
    private final Context context;
    private int historySize;

    public MessageListAdapter(Conversation conversation, Context context) {
        LinkedList<Message> messages = new LinkedList<>();
        if (conversation.getType() != Conversation.TYPE_SERVER) {
            Message header = new Message(conversation.getName());
            header.setColor(Message.COLOR_BLUE);
            messages.add(header);
        }

        messages.addAll(conversation.getHistory());
        conversation.cleanBuffer();

        this.messages = messages;
        this.context = context;
        historySize = conversation.getHistorySize();
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
